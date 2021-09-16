package com.brennaswitzer.cookbook.domain

import javax.persistence.Embeddable
import javax.persistence.Embedded

@Embeddable
class Photo {
    @Embedded
    var file: S3File? = null

    constructor() {}

    fun clearFile() {
        file = null
    }

    fun hasFile(): Boolean {
        return file != null
    }

    var focusLeft: Float? = null
    var focusTop: Float? = null
    fun hasFocus(): Boolean {
        return focusLeft != null && focusTop != null
    }

    var focusArray: FloatArray?
        get() = if (hasFocus()) floatArrayOf(focusLeft!!, focusTop!!) else null
        set(focus) {
            if (focus == null) return
            require(focus.size == 2) { "Focus arrays must have two components" }
            focusLeft = focus[0]
            focusTop = focus[1]
        }

    constructor(file: S3File?) {
        this.file = file
    }

    val objectKey: String
        get() = file!!.objectKey!!
    val contentType: String
        get() = file!!.contentType!!
    val size: Long
        get() = file!!.size!!
}
