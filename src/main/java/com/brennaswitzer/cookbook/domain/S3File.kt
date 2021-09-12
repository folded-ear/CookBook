package com.brennaswitzer.cookbook.domain

import java.util.regex.Pattern
import javax.persistence.Embeddable

@Embeddable
class S3File {
    var objectKey: String? = null
    var contentType: String? = null
    var size // needs to be nullable for historical data
            : Long? = null

    constructor(objectKey: String?, contentType: String?, size: Long?) {
        this.objectKey = objectKey
        this.contentType = contentType
        this.size = size
    }

    constructor() {}

    companion object {
        private val FILENAME_SANITIZER = Pattern.compile("[^a-zA-Z0-9.\\-]+")

        @JvmStatic
        fun sanitizeFilename(filename: String?): String {
            return FILENAME_SANITIZER.matcher(filename).replaceAll("_")
        }
    }
}
