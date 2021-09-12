package com.brennaswitzer.cookbook.payload

class TaskCreate : TaskName() {
    var fromId: Long? = null

    fun hasFromId(): Boolean {
        return fromId != null
    }
}
