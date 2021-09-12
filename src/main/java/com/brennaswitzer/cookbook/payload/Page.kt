package com.brennaswitzer.cookbook.payload

import org.springframework.data.domain.Slice

class Page<E>(
    var page: Int,
    var pageSize: Int,
    var isFirst: Boolean,
    var isLast: Boolean,
    var content: List<E>
) {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Page<*>) return false
        val other = other
        if (!other.canEqual(this as Any)) return false
        if (page != other.page) return false
        if (pageSize != other.pageSize) return false
        if (isFirst != other.isFirst) return false
        if (isLast != other.isLast) return false
        val `this$content`: Any = content
        val `other$content`: Any = other.content
        return if (if (`this$content` == null) `other$content` != null else `this$content` != `other$content`) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is Page<*>
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        result = result * PRIME + page
        result = result * PRIME + pageSize
        result = result * PRIME + if (isFirst) 79 else 97
        result = result * PRIME + if (isLast) 79 else 97
        val `$content`: Any = content
        result = result * PRIME + (`$content`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "Page(page=" + page + ", pageSize=" + pageSize + ", first=" + isFirst + ", last=" + isLast + ", content=" + content + ")"
    }

    companion object {
        @JvmStatic
        fun <E> from(slice: Slice<E>): Page<E> {
            return Page(
                slice.number,
                slice.size,
                slice.isFirst,
                slice.isLast,
                slice.content
            )
        }
    }
}
