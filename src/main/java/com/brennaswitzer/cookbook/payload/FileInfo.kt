package com.brennaswitzer.cookbook.payload

import com.brennaswitzer.cookbook.domain.S3File
import com.brennaswitzer.cookbook.services.StorageService

class FileInfo {
    var filename: String? = null
    var url: String? = null
    var contentType: String? = null
    var size: Long? = null

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is FileInfo) return false
        if (!other.canEqual(this as Any)) return false
        val `this$filename`: Any? = filename
        val `other$filename`: Any? = other.filename
        if (if (`this$filename` == null) `other$filename` != null else `this$filename` != `other$filename`) return false
        val `this$url`: Any? = url
        val `other$url`: Any? = other.url
        if (if (`this$url` == null) `other$url` != null else `this$url` != `other$url`) return false
        val `this$contentType`: Any? = contentType
        val `other$contentType`: Any? = other.contentType
        if (if (`this$contentType` == null) `other$contentType` != null else `this$contentType` != `other$contentType`) return false
        val `this$size`: Any? = size
        val `other$size`: Any? = other.size
        return if (if (`this$size` == null) `other$size` != null else `this$size` != `other$size`) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is FileInfo
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$filename`: Any? = filename
        result = result * PRIME + (`$filename`?.hashCode() ?: 43)
        val `$url`: Any? = url
        result = result * PRIME + (`$url`?.hashCode() ?: 43)
        val `$contentType`: Any? = contentType
        result = result * PRIME + (`$contentType`?.hashCode() ?: 43)
        val `$size`: Any? = size
        result = result * PRIME + (`$size`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "FileInfo(filename=" + filename + ", url=" + url + ", contentType=" + contentType + ", size=" + size + ")"
    }

    companion object {
        @JvmStatic
        fun fromS3File(file: S3File, storage: StorageService): FileInfo {
            val info = FileInfo()
            val url = storage.load(file.objectKey)
            info.url = url
            val i = url.lastIndexOf("/")
            if (i > 0 && i < url.length - 1) {
                info.filename = url.substring(i + 1)
            }
            info.contentType = file.contentType
            info.size = file.size
            return info
        }
    }
}
