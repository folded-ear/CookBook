package com.brennaswitzer.cookbook.payload

open class TaskName {
    var name: String? = null

    constructor() {}
    constructor(name: String) {
        this.name = name
    }
}
