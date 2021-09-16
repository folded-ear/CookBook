package com.brennaswitzer.cookbook.message

import java.time.LocalDate

class CreatePlanBucket {
    var id: Any? = null
    var name: String? = null
    var date: LocalDate? = null

    constructor(id: Any?, name: String?, date: LocalDate?) {
        this.id = id
        this.name = name
        this.date = date
    }

    constructor() {}

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is CreatePlanBucket) return false
        if (!other.canEqual(this as Any)) return false
        val `this$id` = id
        val `other$id` = other.id
        if (if (`this$id` == null) `other$id` != null else `this$id` != `other$id`) return false
        val `this$name`: Any? = name
        val `other$name`: Any? = other.name
        if (if (`this$name` == null) `other$name` != null else `this$name` != `other$name`) return false
        val `this$date`: Any? = date
        val `other$date`: Any? = other.date
        return if (if (`this$date` == null) `other$date` != null else `this$date` != `other$date`) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is CreatePlanBucket
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$id` = id
        result = result * PRIME + (`$id`?.hashCode() ?: 43)
        val `$name`: Any? = name
        result = result * PRIME + (`$name`?.hashCode() ?: 43)
        val `$date`: Any? = date
        result = result * PRIME + (`$date`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "CreatePlanBucket(id=" + id + ", name=" + name + ", date=" + date + ")"
    }
}
