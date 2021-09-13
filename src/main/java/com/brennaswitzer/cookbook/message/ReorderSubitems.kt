package com.brennaswitzer.cookbook.message

class ReorderSubitems {
    var id: Long? = null
    var subitemIds: List<Long>? = null

    constructor(id: Long?, subitemIds: List<Long>?) {
        this.id = id
        this.subitemIds = subitemIds
    }

    constructor() {}

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is ReorderSubitems) return false
        val other = other
        if (!other.canEqual(this as Any)) return false
        val `this$id`: Any? = id
        val `other$id`: Any? = other.id
        if (if (`this$id` == null) `other$id` != null else `this$id` != `other$id`) return false
        val `this$subitemIds`: Any? = subitemIds
        val `other$subitemIds`: Any? = other.subitemIds
        return if (if (`this$subitemIds` == null) `other$subitemIds` != null else `this$subitemIds` != `other$subitemIds`) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is ReorderSubitems
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$id`: Any? = id
        result = result * PRIME + (`$id`?.hashCode() ?: 43)
        val `$subitemIds`: Any? = subitemIds
        result = result * PRIME + (`$subitemIds`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "ReorderSubitems(id=" + id + ", subitemIds=" + subitemIds + ")"
    }
}
