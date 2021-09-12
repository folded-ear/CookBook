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
open class Task : BaseEntity, MutableItem {
    var name: @NotNull String? = null
    var notes: String? = null

    @Column(name = "status_id")
    var status = TaskStatus.NEEDED

    @Embedded
    private var quantity: Quantity? = null
    private var preparation: String? = null
    var position = 0

    @ManyToOne
    private var parent: Task? = null

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL])
    @BatchSize(size = 100)
    private var subtasks: MutableSet<Task?>? = null

    @ManyToOne
    private var aggregate: Task? = null

    @OneToMany(
        mappedBy = "aggregate",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH]
    )
    @BatchSize(size = 100)
    private var components: MutableSet<Task?>? = null

    @ManyToOne(cascade = [CascadeType.MERGE])
    private var ingredient: Ingredient? = null

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
        setQuantity(quantity!!)
        setIngredient(ingredient)
        setPreparation(preparation!!)
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
        get() = getParent() != null
    val isComponent: Boolean
        get() = getAggregate() != null

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
            t = t.getParent()
        }
        return false
    }

    fun isDescendantComponent(t: Task?): Boolean {
        var t = t
        while (t != null) {
            if (t === this) return true
            t = t.getAggregate()
        }
        return false
    }

    open fun setParent(parent: Task?) {
        // see if it's a no-op
        if (parent?.equals(getParent()) ?: (getParent() == null)) {
            return
        }
        require(!isDescendant(parent)) { "You can't make a task a descendant of one of its own descendants" }
        // tear down the old one
        if (getParent() != null && getParent()!!.subtasks != null) {
            check(getParent()!!.subtasks!!.remove(this)) { "Task #" + id + " wasn't a subtask of its parent #" + getParent()!!.id + "?!" }
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
        this.parent = parent
    }

    fun setAggregate(agg: Task?) {
        if (agg?.equals(getAggregate()) ?: (getAggregate() == null)) {
            return
        }
        require(!isDescendantComponent(agg)) { "You can't make a task a component of one of its own components" }
        if (getAggregate() != null && getAggregate()!!.components != null) {
            check(getAggregate()!!.components!!.remove(this)) { "Task #" + id + " wasn't a component of its aggregate #" + getAggregate()!!.id + "?!" }
        }
        if (agg != null) {
            if (agg.components == null) {
                agg.components = HashSet()
            }
        }
        aggregate = agg
    }

    fun hasParent(): Boolean {
        return getParent() != null
    }

    open val taskList: TaskList?
        get() = getParent()!!.taskList

    /**
     * Add a new Task to the end of this list.
     * @param task the task to add.
     */
    fun addSubtask(task: Task?) {
        requireNotNull(task) { "You can't add the null subtask" }
        task.setParent(this)
    }

    /**
     * Add a new Task as both a child and component of this task.
     * @param t the task to add as a component
     */
    fun addAggregateComponent(t: Task) {
        addSubtask(t)
        t.setAggregate(this)
    }

    fun addSubtaskAfter(task: Task?, after: Task?) {
        requireNotNull(task) { "You can't add the null subtask" }
        require(!(after != null && !this.equals(after.getParent()))) { "The 'after' task isn't a child of this; that makes no sense." }
        if (task.getParent() != null) {
            task.getParent()!!.removeSubtask(task)
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
        task.setParent(null)
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
                .append(getParent()!!.name) // NOT .toString()!
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
        return of(after.getParent(), after)
    }

    override fun getRaw(): String {
        return name!!
    }

    override fun getQuantity(): Quantity {
        return quantity ?: Quantity.ONE
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

    override fun getPreparation(): String {
        return preparation!!
    }

    fun getParent(): Task? {
        return parent
    }

    fun getAggregate(): Task? {
        return aggregate
    }

    override fun getIngredient(): Ingredient {
        return ingredient!!
    }

    override fun setQuantity(quantity: Quantity) {
        this.quantity = quantity
    }

    override fun setPreparation(preparation: String) {
        this.preparation = preparation
    }

    override fun setIngredient(ingredient: Ingredient) {
        this.ingredient = ingredient
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
