package com.brennaswitzer.cookbook.message

import com.brennaswitzer.cookbook.payload.IngredientInfo

class IngredientMessage {
    var type: String? = null
    var id: Long? = null
    var info: IngredientInfo? = null

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is IngredientMessage) return false
        val other = other
        if (!other.canEqual(this as Any)) return false
        val `this$type`: Any? = type
        val `other$type`: Any? = other.type
        if (if (`this$type` == null) `other$type` != null else `this$type` != `other$type`) return false
        val `this$id`: Any? = id
        val `other$id`: Any? = other.id
        if (if (`this$id` == null) `other$id` != null else `this$id` != `other$id`) return false
        val `this$info`: Any? = info
        val `other$info`: Any? = other.info
        return if (if (`this$info` == null) `other$info` != null else `this$info` != `other$info`) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is IngredientMessage
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$type`: Any? = type
        result = result * PRIME + (`$type`?.hashCode() ?: 43)
        val `$id`: Any? = id
        result = result * PRIME + (`$id`?.hashCode() ?: 43)
        val `$info`: Any? = info
        result = result * PRIME + (`$info`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "IngredientMessage(type=" + type + ", id=" + id + ", info=" + info + ")"
    }
}
