package com.brennaswitzer.cookbook.message

import com.brennaswitzer.cookbook.domain.TaskStatus

class SetPlanTreeItemStatus {
    var id: Long? = null
    var status: TaskStatus? = null

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is SetPlanTreeItemStatus) return false
        if (!other.canEqual(this as Any)) return false
        val `this$id`: Any? = id
        val `other$id`: Any? = other.id
        if (if (`this$id` == null) `other$id` != null else `this$id` != `other$id`) return false
        val `this$status`: Any? = status
        val `other$status`: Any? = other.status
        return if (if (`this$status` == null) `other$status` != null else `this$status` != `other$status`) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is SetPlanTreeItemStatus
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$id`: Any? = id
        result = result * PRIME + (`$id`?.hashCode() ?: 43)
        val `$status`: Any? = status
        result = result * PRIME + (`$status`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "SetPlanTreeItemStatus(id=" + id + ", status=" + status + ")"
    }
}
