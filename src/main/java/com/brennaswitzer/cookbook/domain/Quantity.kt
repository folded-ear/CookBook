package com.brennaswitzer.cookbook.domain

import com.brennaswitzer.cookbook.util.NumberUtils
import java.util.*
import javax.persistence.*

@Embeddable
@Access(AccessType.FIELD) // It is unclear why this is needed. The only JPA
// annotation at field or prop level is on a field.
// Clearly still something to learn here. :)
class Quantity {
    @Column(nullable = true)
    var quantity = 0.0

    @ManyToOne
    var units: UnitOfMeasure? = null

    constructor() {}
    constructor(quantity: Number, units: UnitOfMeasure?) {
        this.quantity = quantity.toDouble()
        this.units = units
    }

    fun hasUnits(): Boolean {
        return units != null
    }

    fun convertTo(uom: UnitOfMeasure?): Quantity {
        // this isn't really needed, but it'll save some allocation
        if (units!!.hasConversion(uom!!)) {
            return Quantity(
                quantity * units!!.getConversion(uom),
                uom
            )
        }
        // no direct conversion; start walking
        val queue: Queue<Quantity> = LinkedList()
        val visited: MutableSet<UnitOfMeasure?> = HashSet()
        queue.add(this)
        while (!queue.isEmpty()) {
            val q = queue.remove()
            if (!visited.add(q.units)) continue
            if (q.units!!.equals(uom)) return q
            for ((key, value) in q.units!!.conversions) {
                if (visited.contains(key)) continue
                queue.add(
                    Quantity(
                        q.quantity * value,
                        key
                    )
                )
            }
        }
        throw NoConversionException(units, uom)
    }

    operator fun plus(that: Quantity): Quantity {
        if (0.0 == quantity) return that
        if (0.0 == that.quantity) return this
        return if (units == that.units) {
            Quantity(quantity + that.quantity, units)
        } else plus(that.convertTo(units))
    }

    operator fun minus(that: Quantity) =
        this + Quantity(-that.quantity, that.units)

    operator fun times(factor: Double): Quantity {
        return if (factor == 1.0) this else Quantity(
            quantity * factor,
            units
        )
    }

    override fun toString(): String {
        val qs = NumberUtils.formatNumber(quantity)
        return if (hasUnits()) qs + " " + units!!.name else qs
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Quantity) return false
        val that = other
        return (units == that.units
                && Math.abs(quantity - that.quantity) < 0.001)
    }

    override fun hashCode(): Int {
        return Objects.hash(quantity, units)
    }

    companion object {
        @JvmField
        val ZERO = count(0.0)
        @JvmField
        val ONE = count(1.0)
        @JvmStatic
        fun count(count: Double): Quantity {
            return Quantity(count, null)
        }
    }
}
