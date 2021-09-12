package com.brennaswitzer.cookbook.domain

import org.hibernate.annotations.BatchSize
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@DiscriminatorValue("plan")
class TaskList : Task, AccessControlled {
    @Embedded
    private var acl: @NotNull Acl? = Acl()

    @OneToMany(
        mappedBy = "plan",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @BatchSize(size = 100)
    private var buckets: Set<PlanBucket>? = null

    constructor() {}
    constructor(name: String?) : super(name) {}
    constructor(owner: User?, name: String?) : super(name) {
        setOwner(owner)
    }

    override fun setParent(parent: Task?) {
        throw UnsupportedOperationException("TaskLists can't have parents")
    }

    override val taskList: TaskList
        get() = this

    fun getBuckets(): Set<PlanBucket> {
        if (buckets == null) {
            buckets = HashSet()
        }
        return buckets!!
    }

    fun hasBuckets(): Boolean {
        return buckets != null && !buckets!!.isEmpty()
    }

    override fun getAcl(): @NotNull Acl? {
        return acl
    }

    fun setAcl(acl: @NotNull Acl?) {
        this.acl = acl
    }
}
