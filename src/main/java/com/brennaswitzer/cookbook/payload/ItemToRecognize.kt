package com.brennaswitzer.cookbook.payload

class ItemToRecognize {
    var raw: String? = null
    var cursor: Int? = null
        get() = field ?: raw!!.length
}
