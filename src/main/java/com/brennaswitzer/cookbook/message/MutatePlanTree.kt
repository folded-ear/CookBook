package com.brennaswitzer.cookbook.message

class MutatePlanTree {
    var ids: List<Long>? = null
    var parentId: Long? = null
    var afterId: Long? = null

    constructor(ids: List<Long>?, parentId: Long?, afterId: Long?) {
        this.ids = ids
        this.parentId = parentId
        this.afterId = afterId
    }

    constructor() {}

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is MutatePlanTree) return false
        val other = other
        if (!other.canEqual(this as Any)) return false
        val `this$ids`: Any? = ids
        val `other$ids`: Any? = other.ids
        if (if (`this$ids` == null) `other$ids` != null else `this$ids` != `other$ids`) return false
        val `this$parentId`: Any? = parentId
        val `other$parentId`: Any? = other.parentId
        if (if (`this$parentId` == null) `other$parentId` != null else `this$parentId` != `other$parentId`) return false
        val `this$afterId`: Any? = afterId
        val `other$afterId`: Any? = other.afterId
        return if (if (`this$afterId` == null) `other$afterId` != null else `this$afterId` != `other$afterId`) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is MutatePlanTree
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$ids`: Any? = ids
        result = result * PRIME + (`$ids`?.hashCode() ?: 43)
        val `$parentId`: Any? = parentId
        result = result * PRIME + (`$parentId`?.hashCode() ?: 43)
        val `$afterId`: Any? = afterId
        result = result * PRIME + (`$afterId`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "MutatePlanTree(ids=" + ids + ", parentId=" + parentId + ", afterId=" + afterId + ")"
    }
}
