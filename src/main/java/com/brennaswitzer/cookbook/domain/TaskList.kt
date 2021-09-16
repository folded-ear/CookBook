package com.brennaswitzer.cookbook.domain

import org.hibernate.annotations.BatchSize
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@DiscriminatorValue("plan")
class TaskList : Task, AccessControlled {
    @Embedded
    override var acl: @NotNull Acl? = Acl()

    @OneToMany(
        mappedBy = "plan",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        targetEntity = PlanBucket::class,
    )
    @BatchSize(size = 100)
    var buckets: Set<PlanBucket>? = null
        get() {
            if (field == null) {
                field = HashSet()
            }
            return field!!
        }

    constructor() {}
    constructor(name: String?) : super(name) {}
    constructor(owner: User?, name: String?) : super(name) {
        this.owner = owner
    }

    override var parent
        get() = super.parent
        set(parent) =
            throw UnsupportedOperationException("TaskLists can't have parents")

    override val taskList: TaskList
        get() = this

    fun hasBuckets(): Boolean {
        return buckets != null && !buckets!!.isEmpty()
    }

}
