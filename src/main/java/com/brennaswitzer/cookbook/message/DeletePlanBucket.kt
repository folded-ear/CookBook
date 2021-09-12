package com.brennaswitzer.cookbook.message

class DeletePlanBucket {
    var id: Long? = null

    constructor(id: Long?) {
        this.id = id
    }

    constructor() {}

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is DeletePlanBucket) return false
        val other = o
        if (!other.canEqual(this as Any)) return false
        val `this$id`: Any? = id
        val `other$id`: Any? = other.id
        return if (if (`this$id` == null) `other$id` != null else `this$id` != `other$id`) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is DeletePlanBucket
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$id`: Any? = id
        result = result * PRIME + (`$id`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "DeletePlanBucket(id=" + id + ")"
    }
}
