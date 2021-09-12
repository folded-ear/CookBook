package com.brennaswitzer.cookbook.message

class AssignPlanTreeItemBucket {
    var id: Long? = null
    var bucketId: Long? = null

    constructor(id: Long?, bucketId: Long?) {
        this.id = id
        this.bucketId = bucketId
    }

    constructor() {}

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is AssignPlanTreeItemBucket) return false
        val other = o
        if (!other.canEqual(this as Any)) return false
        val `this$id`: Any? = id
        val `other$id`: Any? = other.id
        if (if (`this$id` == null) `other$id` != null else `this$id` != `other$id`) return false
        val `this$bucketId`: Any? = bucketId
        val `other$bucketId`: Any? = other.bucketId
        return if (if (`this$bucketId` == null) `other$bucketId` != null else `this$bucketId` != `other$bucketId`) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is AssignPlanTreeItemBucket
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$id`: Any? = id
        result = result * PRIME + (`$id`?.hashCode() ?: 43)
        val `$bucketId`: Any? = bucketId
        result = result * PRIME + (`$bucketId`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "AssignPlanTreeItemBucket(id=" + id + ", bucketId=" + bucketId + ")"
    }
}
