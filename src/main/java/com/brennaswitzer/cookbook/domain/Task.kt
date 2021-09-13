package com.brennaswitzer.cookbook.domain

import org.hibernate.annotations.BatchSize
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "_type")
@DiscriminatorValue("item")
class Task : BaseEntity, MutableItem {
    var name: @NotNull String? = null
    override val raw: String?
        get() = name

    var notes: String? = null

    @Column(name = "status_id")
    var status = TaskStatus.NEEDED

    @Embedded
    override var quantity: Quantity? = null
    override var preparation: String? = null
    var position = 0

    @ManyToOne
    var parent: Task? = null
        set(parent) {
            // see if it's a no-op
            if (parent?.equals(field) ?: (field == null)) {
                return
            }
            require(!isDescendant(parent)) { "You can't make a task a descendant of one of its own descendants" }
            // tear down the old one
            if (field != null && field!!.subtasks != null) {
                check(field!!.subtasks!!.remove(this)) { "Task #" + id + " wasn't a subtask of its parent #" + field!!.id + "?!" }
            }
            // wire up the new one
            if (parent != null) {
                if (parent.subtasks == null) {
                    parent.subtasks = HashSet()
                }
                if (parent.subtasks!!.add(this)) {
                    position = 1 + parent.subtasks!!
                        .stream()
                        .map { obj: Task? -> obj!!.position }
                        .reduce(0) { a: Int, b: Int -> Integer.max(a, b) }
                }
            }
            field = parent
        }

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL])
    @BatchSize(size = 100)
    var subtasks: MutableSet<Task?>? = null

    @ManyToOne
    var aggregate: Task? = null
        set(agg) {
            if (agg?.equals(field) ?: (field == null)) {
                return
            }
            require(!isDescendantComponent(agg)) { "You can't make a task a component of one of its own components" }
            if (field != null && field!!.components != null) {
                check(field!!.components!!.remove(this)) { "Task #" + id + " wasn't a component of its aggregate #" + field!!.id + "?!" }
            }
            if (agg != null) {
                if (agg.components == null) {
                    agg.components = HashSet()
                }
            }
            field = agg
        }

    @OneToMany(
        mappedBy = "aggregate",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH]
    )
    @BatchSize(size = 100)
    var components: MutableSet<Task?>? = null

    @ManyToOne(cascade = [CascadeType.MERGE])
    override var ingredient: Ingredient? = null

    @ManyToOne
    var bucket: PlanBucket? = null

    constructor() {}
    constructor(name: String?) {
        this.name = name
    }

    internal constructor(name: String?, position: Int) {
        this.name = name
        this.position = position
    }

    constructor(
        name: String?,
        quantity: Quantity?,
        ingredient: Ingredient,
        preparation: String?
    ) {
        this.name = name
        this.quantity = quantity
        this.ingredient = ingredient
        this.preparation = preparation
    }

    constructor(name: String?, ingredient: Ingredient) : this(
        name,
        null,
        ingredient,
        null
    ) {
    }

    fun setChildPosition(child: Task, position: Int) {
        val seq = AtomicInteger()
        var pending = true
        for (t in orderedSubtasksView) {
            if (t!!.equals(child)) continue
            var min = seq.getAndIncrement()
            if (pending && min >= position) {
                pending = false
                child.position = position
                min = seq.getAndIncrement()
            }
            val curr = t.position
            if (curr < min) {
                t.position = min
            } else if (curr > min) {
                seq.set(curr + 1)
            }
        }
        if (pending) {
            child.position = seq.get()
        }
    }

    val isSubtask: Boolean
        get() = this.parent != null
    val isComponent: Boolean
        get() = this.aggregate != null

    fun hasSubtasks(): Boolean {
        return subtaskCount != 0
    }

    fun hasComponents(): Boolean {
        return componentCount != 0
    }

    fun isDescendant(t: Task?): Boolean {
        var t = t
        while (t != null) {
            if (t === this) return true
            t = t.parent
        }
        return false
    }

    fun isDescendantComponent(t: Task?): Boolean {
        var t = t
        while (t != null) {
            if (t === this) return true
            t = t.aggregate
        }
        return false
    }

    fun hasParent(): Boolean {
        return this.parent != null
    }

    val taskList: TaskList?
        get() = this.parent!!.taskList

    /**
     * Add a new Task to the end of this list.
     * @param task the task to add.
     */
    fun addSubtask(task: Task?) {
        requireNotNull(task) { "You can't add the null subtask" }
        task.parent = this
    }

    /**
     * Add a new Task as both a child and component of this task.
     * @param t the task to add as a component
     */
    fun addAggregateComponent(t: Task) {
        addSubtask(t)
        t.aggregate = this
    }

    fun addSubtaskAfter(task: Task?, after: Task?) {
        requireNotNull(task) { "You can't add the null subtask" }
        require(!(after != null && !this.equals(after.parent))) { "The 'after' task isn't a child of this; that makes no sense." }
        if (task.parent != null) {
            task.parent!!.removeSubtask(task)
        }
        val position = if (after == null) 0 else after.position + 1
        insertSubtask(position, task)
    }

    fun insertSubtask(position: Int, task: Task?) {
        require(position >= 0) { "You can't insert a task at a negative position" }
        requireNotNull(task) { "You can't add the null subtask" }
        addSubtask(task)
        setChildPosition(task, position)
    }

    fun removeSubtask(task: Task?) {
        requireNotNull(task) { "You can't remove the null subtask" }
        task.parent = null
    }

    val subtaskView: Collection<Task?>
        get() = if (subtasks == null) {
            Collections.emptySet()
        } else Collections.unmodifiableSet(
            subtasks
        )
    val orderedSubtasksView: List<Task?>
        get() = getSubtaskView(BY_ORDER)

    fun getSubtaskView(comparator: Comparator<Task?>?): List<Task?> {
        if (subtasks == null) {
            return Collections.emptyList()
        }
        val list: MutableList<Task?> = ArrayList(subtasks)
        Collections.sort(list, comparator)
        return list
    }

    val orderedComponentsView: List<Task?>
        get() = getComponentView(BY_ID)

    fun getComponentView(comparator: Comparator<Task?>?): List<Task?> {
        if (components == null) {
            return Collections.emptyList()
        }
        val list: MutableList<Task?> = ArrayList(components)
        Collections.sort(list, comparator)
        return list
    }

    val subtaskCount: Int
        get() = if (subtasks == null) 0 else subtasks!!.size
    val componentCount: Int
        get() = if (components == null) 0 else components!!.size

    override fun toString(): String {
        val sb = StringBuilder(name)
        if (isSubtask) {
            sb.append(" [")
                .append(parent!!.name) // NOT .toString()!
                .append(']')
        }
        return sb.toString()
    }

    fun of(parent: Task): Task {
        parent.addSubtask(this)
        return this
    }

    fun of(parent: Task?, after: Task?): Task {
        parent!!.addSubtaskAfter(this, after)
        return this
    }

    fun after(after: Task): Task {
        return of(after.parent, after)
    }

    fun hasIngredient(): Boolean {
        return ingredient != null
    }

    fun hasBucket(): Boolean {
        return bucket != null
    }

    fun hasNotes(): Boolean {
        return notes != null && !notes!!.isEmpty()
    }

    companion object {
        @JvmField
        val BY_ID = Comparator { a: Task?, b: Task? ->
            if (a == null) return@Comparator if (b == null) 0 else 1
            if (b == null) return@Comparator -1
            a.id!!.compareTo(b.id!!)
        }
        @JvmField
        val BY_NAME = Comparator { a: Task?, b: Task? ->
            if (a == null) return@Comparator if (b == null) 0 else 1
            if (b == null) return@Comparator -1
            a.name!!.compareTo(b.name!!)
        }
        @JvmField
        val BY_NAME_IGNORE_CASE = Comparator { a: Task?, b: Task? ->
            if (a == null) return@Comparator if (b == null) 0 else 1
            if (b == null) return@Comparator -1
            a.name!!.compareTo(b.name!!, ignoreCase = true)
        }
        @JvmField
        val BY_ORDER = Comparator { a: Task?, b: Task? ->
            if (a == null) return@Comparator if (b == null) 0 else 1
            if (b == null) return@Comparator -1
            val c = a.position - b.position
            if (c != 0) return@Comparator c
            a.name!!.compareTo(b.name!!, ignoreCase = true)
        }
    }
}
