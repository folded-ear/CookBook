package com.brennaswitzer.cookbook.domain

import javax.persistence.*

@Entity
class TextractJob : BaseEntity() {
    @Embeddable
    class Line {
        var text: String? = null

        // left is a SQL keyword, so to make querying easier, use x/y columns
        @Embedded
        @AttributeOverrides(
            AttributeOverride(
                name = "left",
                column = Column(name = "x")
            ), AttributeOverride(name = "top", column = Column(name = "y"))
        )
        var box: Box? = null

        constructor(text: String?, box: Box?) {
            this.text = text
            this.box = box
        }

        constructor() {}

        override fun equals(other: Any?): Boolean {
            if (other === this) return true
            if (other !is Line) return false
            val other = other
            if (!other.canEqual(this as Any)) return false
            val `this$text`: Any? = text
            val `other$text`: Any? = other.text
            if (if (`this$text` == null) `other$text` != null else `this$text` != `other$text`) return false
            val `this$box`: Any? = box
            val `other$box`: Any? = other.box
            return if (if (`this$box` == null) `other$box` != null else `this$box` != `other$box`) false else true
        }

        protected fun canEqual(other: Any?): Boolean {
            return other is Line
        }

        override fun hashCode(): Int {
            val PRIME = 59
            var result = 1
            val `$text`: Any? = text
            result = result * PRIME + (`$text`?.hashCode() ?: 43)
            val `$box`: Any? = box
            result = result * PRIME + (`$box`?.hashCode() ?: 43)
            return result
        }

        override fun toString(): String {
            return "TextractJob.Line(text=" + text + ", box=" + box + ")"
        }
    }

    @Embeddable
    class Box {
        var left = 0.0
        var top = 0.0
        var width = 0.0
        var height = 0.0

        constructor(left: Double, top: Double, width: Double, height: Double) {
            this.left = left
            this.top = top
            this.width = width
            this.height = height
        }

        constructor() {}

        override fun equals(other: Any?): Boolean {
            if (other === this) return true
            if (other !is Box) return false
            val other = other
            if (!other.canEqual(this as Any)) return false
            if (java.lang.Double.compare(left, other.left) != 0) return false
            if (java.lang.Double.compare(top, other.top) != 0) return false
            if (java.lang.Double.compare(width, other.width) != 0) return false
            return if (java.lang.Double.compare(
                    height,
                    other.height
                ) != 0
            ) false else true
        }

        protected fun canEqual(other: Any?): Boolean {
            return other is Box
        }

        override fun hashCode(): Int {
            val PRIME = 59
            var result = 1
            val `$left` = java.lang.Double.doubleToLongBits(left)
            result = result * PRIME + (`$left` ushr 32 xor `$left`).toInt()
            val `$top` = java.lang.Double.doubleToLongBits(top)
            result = result * PRIME + (`$top` ushr 32 xor `$top`).toInt()
            val `$width` = java.lang.Double.doubleToLongBits(width)
            result = result * PRIME + (`$width` ushr 32 xor `$width`).toInt()
            val `$height` = java.lang.Double.doubleToLongBits(height)
            result = result * PRIME + (`$height` ushr 32 xor `$height`).toInt()
            return result
        }

        override fun toString(): String {
            return "TextractJob.Box(left=" + left + ", top=" + top + ", width=" + width + ", height=" + height + ")"
        }
    }

    @ManyToOne(optional = false)
    var owner: User? = null

    @Embedded
    var photo: S3File? = null
    fun hasPhoto(): Boolean {
        return photo != null
    }

    var ready = false

    fun isReady() = ready

    @ElementCollection
    var lines: Set<Line>? = null
}
