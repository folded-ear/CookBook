package com.brennaswitzer.cookbook.message

class OrderForStore {
    var id: Long? = null
    var targetId: Long? = null
    var isAfter = true
    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is OrderForStore) return false
        val other = o
        if (!other.canEqual(this as Any)) return false
        val `this$id`: Any? = id
        val `other$id`: Any? = other.id
        if (if (`this$id` == null) `other$id` != null else `this$id` != `other$id`) return false
        val `this$targetId`: Any? = targetId
        val `other$targetId`: Any? = other.targetId
        if (if (`this$targetId` == null) `other$targetId` != null else `this$targetId` != `other$targetId`) return false
        return if (isAfter != other.isAfter) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is OrderForStore
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$id`: Any? = id
        result = result * PRIME + (`$id`?.hashCode() ?: 43)
        val `$targetId`: Any? = targetId
        result = result * PRIME + (`$targetId`?.hashCode() ?: 43)
        result = result * PRIME + if (isAfter) 79 else 97
        return result
    }

    override fun toString(): String {
        return "OrderForStore(id=" + id + ", targetId=" + targetId + ", after=" + isAfter + ")"
    }
}
