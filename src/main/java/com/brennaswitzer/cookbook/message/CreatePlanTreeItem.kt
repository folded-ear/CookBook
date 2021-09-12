package com.brennaswitzer.cookbook.message

class CreatePlanTreeItem {
    var id: Any? = null
    var parentId: Long? = null
    var afterId: Long? = null
    var name: String? = null

    constructor(id: Any?, parentId: Long?, afterId: Long?, name: String?) {
        this.id = id
        this.parentId = parentId
        this.afterId = afterId
        this.name = name
    }

    constructor() {}

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is CreatePlanTreeItem) return false
        val other = o
        if (!other.canEqual(this as Any)) return false
        val `this$id` = id
        val `other$id` = other.id
        if (if (`this$id` == null) `other$id` != null else `this$id` != `other$id`) return false
        val `this$parentId`: Any? = parentId
        val `other$parentId`: Any? = other.parentId
        if (if (`this$parentId` == null) `other$parentId` != null else `this$parentId` != `other$parentId`) return false
        val `this$afterId`: Any? = afterId
        val `other$afterId`: Any? = other.afterId
        if (if (`this$afterId` == null) `other$afterId` != null else `this$afterId` != `other$afterId`) return false
        val `this$name`: Any? = name
        val `other$name`: Any? = other.name
        return if (if (`this$name` == null) `other$name` != null else `this$name` != `other$name`) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is CreatePlanTreeItem
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$id` = id
        result = result * PRIME + (`$id`?.hashCode() ?: 43)
        val `$parentId`: Any? = parentId
        result = result * PRIME + (`$parentId`?.hashCode() ?: 43)
        val `$afterId`: Any? = afterId
        result = result * PRIME + (`$afterId`?.hashCode() ?: 43)
        val `$name`: Any? = name
        result = result * PRIME + (`$name`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "CreatePlanTreeItem(id=" + id + ", parentId=" + parentId + ", afterId=" + afterId + ", name=" + name + ")"
    }
}
