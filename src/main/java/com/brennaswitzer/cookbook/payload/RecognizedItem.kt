package com.brennaswitzer.cookbook.payload

import com.brennaswitzer.cookbook.util.EnglishUtils
import java.util.*

class RecognizedItem {
    var raw: String? = null
    var cursor = 0
    val ranges: MutableSet<Range> by lazy {
        TreeSet(Range.BY_POSITION)
    }

    val suggestions: MutableSet<Suggestion> by lazy {
        TreeSet(Suggestion.BY_POSITION_AND_NAME)
    }

    @JvmOverloads
    constructor(raw: String, cursor: Int = raw.length) {
        this.raw = raw
        this.cursor = Math.min(Math.max(cursor, 0), raw.length)
    }

    constructor() {}

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is RecognizedItem) return false
        if (!other.canEqual(this as Any)) return false
        val `this$raw`: Any? = raw
        val `other$raw`: Any? = other.raw
        if (if (`this$raw` == null) `other$raw` != null else `this$raw` != `other$raw`) return false
        if (cursor != other.cursor) return false
        val `this$ranges`: Any = this.ranges
        val `other$ranges`: Any = other.ranges
        if (`this$ranges` != `other$ranges`) return false
        val `this$suggestions`: Any = suggestions
        val `other$suggestions`: Any = other.suggestions
        return if (`this$suggestions` != `other$suggestions`) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is RecognizedItem
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$raw`: Any? = raw
        result = result * PRIME + (`$raw`?.hashCode() ?: 43)
        result = result * PRIME + cursor
        val `$ranges`: Any = ranges
        result = result * PRIME + (`$ranges`.hashCode() ?: 43)
        val `$suggestions`: Any = suggestions
        result = result * PRIME + (`$suggestions`.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "RecognizedItem(raw=" + raw + ", cursor=" + cursor + ", ranges=" + ranges + ", suggestions=" + suggestions + ")"
    }

    enum class Type {
        UNKNOWN, AMOUNT, UNIT, NEW_UNIT, ITEM, NEW_ITEM
    }

    class Range {
        var start = 0
        var end = 0
        var type: Type? = null
        var value: Any? = null

        @JvmOverloads
        constructor(start: Int, end: Int, type: Type? = Type.UNKNOWN) {
            this.start = start
            this.end = end
            this.type = type
        }

        constructor(start: Int, end: Int, type: Type?, value: Any?) {
            this.start = start
            this.end = end
            this.type = type
            this.value = value
        }

        constructor() {}

        fun of(type: Type?): Range {
            return Range(
                start,
                end,
                type
            )
        }

        fun merge(other: Range): Range {
            return Range(
                start,
                other.end
            )
        }

        fun overlaps(r: Range): Boolean {
            // this wraps r
            if (start <= r.start && end >= r.end) return true
            // this is inside r
            if (start >= r.start && end <= r.end) return true
            // this spans r's start
            if (start <= r.start && end >= r.start) return true
            // this spans r's end
            return if (start <= r.end && end >= r.end) true else false
            // no overlap
        }

        fun withValue(value: Any?): Range {
            this.value = value
            return this
        }

        override fun equals(other: Any?): Boolean {
            if (other === this) return true
            if (other !is Range) return false
            val other = other
            if (!other.canEqual(this as Any)) return false
            if (start != other.start) return false
            if (end != other.end) return false
            val `this$type`: Any? = type
            val `other$type`: Any? = other.type
            return if (if (`this$type` == null) `other$type` != null else `this$type` != `other$type`) false else true
        }

        protected fun canEqual(other: Any?): Boolean {
            return other is Range
        }

        override fun hashCode(): Int {
            val PRIME = 59
            var result = 1
            result = result * PRIME + start
            result = result * PRIME + end
            val `$type`: Any? = type
            result = result * PRIME + (`$type`?.hashCode() ?: 43)
            return result
        }

        override fun toString(): String {
            return "RecognizedItem.Range(start=" + start + ", end=" + end + ", type=" + type + ", value=" + value + ")"
        }

        companion object {
            var BY_POSITION = Comparator.comparingInt { a: Range -> a.start }
        }
    }

    class Suggestion {
        var name: String? = null
        var target: Range? = null

        constructor(name: String?, target: Range?) {
            this.name = name
            this.target = target
        }

        constructor() {}

        override fun equals(other: Any?): Boolean {
            if (other === this) return true
            if (other !is Suggestion) return false
            val other = other
            if (!other.canEqual(this as Any)) return false
            val `this$name`: Any? = name
            val `other$name`: Any? = other.name
            if (if (`this$name` == null) `other$name` != null else `this$name` != `other$name`) return false
            val `this$target`: Any? = target
            val `other$target`: Any? = other.target
            return if (if (`this$target` == null) `other$target` != null else `this$target` != `other$target`) false else true
        }

        protected fun canEqual(other: Any?): Boolean {
            return other is Suggestion
        }

        override fun hashCode(): Int {
            val PRIME = 59
            var result = 1
            val `$name`: Any? = name
            result = result * PRIME + (`$name`?.hashCode() ?: 43)
            val `$target`: Any? = target
            result = result * PRIME + (`$target`?.hashCode() ?: 43)
            return result
        }

        override fun toString(): String {
            return "RecognizedItem.Suggestion(name=" + name + ", target=" + target + ")"
        }

        companion object {
            @JvmField
            var BY_POSITION =
                Comparator.comparingInt { a: Suggestion -> a.target!!.start }

            @JvmField
            var BY_POSITION_AND_NAME = BY_POSITION.thenComparing(
                { a: Suggestion -> a.name },
                java.lang.String.CASE_INSENSITIVE_ORDER
            )
        }
    }

    fun withRange(r: Range): RecognizedItem {
        ranges.add(r)
        return this
    }

    fun withSuggestion(c: Suggestion): RecognizedItem {
        suggestions.add(c)
        return this
    }

    /**
     * I return an Iterable over all the words in the raw string which have not
     * been recognized yet.
     */
    fun unrecognizedWords(): Iterable<Range> {
        return unrecognizedWords(raw)
    }

    fun unrecognizedWordsThrough(endIndex: Int): Iterable<Range> {
        return unrecognizedWords(raw!!.substring(0, endIndex))
    }

    private fun unrecognizedWords(raw: String?): Iterable<Range> {
        val result: MutableList<Range> = LinkedList()
        val words = raw!!.split(" ").toTypedArray()
        var pos = 0
        for (w in words) {
            val c = EnglishUtils.canonicalize(w)
            var r: Range
            r = if (w == c) {
                Range(pos, pos + w.length)
            } else {
                val start = w.indexOf(c)
                Range(
                    pos + start,
                    pos + start + c.length
                )
            }
            if (ranges == null || ranges!!.stream().noneMatch(r::overlaps)) {
                result.add(r)
            }
            pos += w.length + 1 // for the split space
        }
        return result
    }
}
