package com.brennaswitzer.cookbook.message

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class PlanMessage {
    var type: String? = null
    var id: Long? = null
    var info: Any? = null
    var newIds: MutableMap<Long, Any>? = null
    fun addNewId(newId: Long, oldId: Any) {
        if (newIds == null) {
            newIds = HashMap()
        }
        newIds!![newId] = oldId
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is PlanMessage) return false
        val other = other
        if (!other.canEqual(this as Any)) return false
        val `this$type`: Any? = type
        val `other$type`: Any? = other.type
        if (if (`this$type` == null) `other$type` != null else `this$type` != `other$type`) return false
        val `this$id`: Any? = id
        val `other$id`: Any? = other.id
        if (if (`this$id` == null) `other$id` != null else `this$id` != `other$id`) return false
        val `this$info` = info
        val `other$info` = other.info
        if (if (`this$info` == null) `other$info` != null else `this$info` != `other$info`) return false
        val `this$newIds`: Any? = newIds
        val `other$newIds`: Any? = other.newIds
        return if (if (`this$newIds` == null) `other$newIds` != null else `this$newIds` != `other$newIds`) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is PlanMessage
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$type`: Any? = type
        result = result * PRIME + (`$type`?.hashCode() ?: 43)
        val `$id`: Any? = id
        result = result * PRIME + (`$id`?.hashCode() ?: 43)
        val `$info` = info
        result = result * PRIME + (`$info`?.hashCode() ?: 43)
        val `$newIds`: Any? = newIds
        result = result * PRIME + (`$newIds`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "PlanMessage(type=" + type + ", id=" + id + ", info=" + info + ", newIds=" + newIds + ")"
    }
}
