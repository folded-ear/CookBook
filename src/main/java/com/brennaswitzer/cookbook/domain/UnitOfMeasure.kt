package com.brennaswitzer.cookbook.domain

import com.brennaswitzer.cookbook.domain.UnitOfMeasure
import com.brennaswitzer.cookbook.util.EnglishUtils
import org.springframework.util.Assert
import java.util.*
import javax.persistence.*

@Entity
@NamedQuery(
    name = "UnitOfMeasure.byName", query = """select uom
from UnitOfMeasure uom
    left join uom.aliases a
where uom.name = :name
    or uom.pluralName = :name
    or a = :name
order by case :name when uom.name then 1
when uom.pluralName then 2
else 3
end"""
)
class UnitOfMeasure : BaseEntity {
    var name: String? = null
    var pluralName: String? = null

    @ElementCollection
    @Column(name = "alias")
    private var aliases: MutableSet<String?>? = null

    @ElementCollection
    @MapKeyJoinColumn(name = "target_id")
    @Column(name = "factor")
    private val conversions: MutableMap<UnitOfMeasure, Double> = HashMap()

    constructor() {}
    constructor(name: String, vararg aliases: String?) {
        this.name = name
        addAliases(*aliases)
    }

    fun hasAlias(alias: String?): Boolean {
        return if (aliases == null) false else aliases!!.contains(alias)
    }

    fun addAlias(alias: String?): Boolean {
        Assert.notNull(alias, "Can't alias null")
        if (aliases == null) aliases = HashSet()
        return aliases!!.add(alias)
    }

    fun addAliases(vararg aliases: String?) {
        for (a in aliases) addAlias(a)
    }

    fun removeAlias(alias: String?): Boolean {
        return if (aliases == null) false else aliases!!.remove(alias)
    }

    fun getAliases(): Set<String?> {
        return if (aliases == null) {
            Collections.emptySet()
        } else Collections.unmodifiableSet(
            aliases
        )
    }

    fun hasConversion(uom: UnitOfMeasure): Boolean {
        return conversions.containsKey(uom)
    }

    fun getConversion(uom: UnitOfMeasure): Double {
        return conversions[uom]!!
    }

    fun addConversion(uom: UnitOfMeasure, factor: Int): Double? {
        return addConversion(uom, factor.toDouble())
    }

    fun addConversion(uom: UnitOfMeasure, factor: Double): Double? {
        Assert.notNull(uom, "Can't convert to the null UoM")
        Assert.notNull(factor, "UoM conversion factor's can't be null")
        uom.conversions[this] = 1.0 / factor
        return conversions.put(uom, factor)
    }

    fun removeConversion(uom: UnitOfMeasure): Double? {
        return conversions.remove(uom)
    }

    fun getConversions(): Map<UnitOfMeasure, Double> {
        return Collections.unmodifiableMap(conversions)
    }

    override fun toString(): String {
        return name ?: "unnamed unit of measure"
    }

    fun quantity(quantity: Double): Quantity {
        return Quantity(quantity, this)
    }

    fun withAlias(alias: String?): UnitOfMeasure {
        addAlias(alias)
        return this
    }

    companion object {
        @JvmField
        val BY_NAME = Comparator { a: UnitOfMeasure?, b: UnitOfMeasure? ->
            if (a == null) return@Comparator if (b == null) 0 else 1
            if (b == null) return@Comparator -1
            a.name!!.compareTo(b.name!!, ignoreCase = true)
        }

        @JvmStatic
        fun find(
            entityManager: EntityManager,
            name: String?
        ): Optional<UnitOfMeasure> {
            var name = name ?: return Optional.empty()
            name = EnglishUtils.unpluralize(name.trim { it <= ' ' })
            var uoms = entityManager.createNamedQuery(
                "UnitOfMeasure.byName",
                UnitOfMeasure::class.java
            )
                .setParameter("name", name)
                .resultList
            if (!uoms.isEmpty()) return Optional.of(uoms[0])
            // fine. try lowercased
            uoms = entityManager.createNamedQuery(
                "UnitOfMeasure.byName",
                UnitOfMeasure::class.java
            )
                .setParameter("name", name.lowercase(Locale.getDefault()))
                .resultList
            return if (!uoms.isEmpty()) Optional.of(
                uoms[0]
            ) else Optional.empty()
        }

        @JvmStatic
        fun ensure(entityManager: EntityManager, name: String?): UnitOfMeasure {
            if (name == null) throw NullPointerException()
            return find(entityManager, name)
                .orElseGet {
                    val uom = UnitOfMeasure(
                        EnglishUtils.unpluralize(name.trim { it <= ' ' })
                    )
                    entityManager.persist(uom)
                    uom
                }
        }
    }
}
