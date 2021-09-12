package com.brennaswitzer.cookbook.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = PantryItem::class, name = "PantryItem"),
    JsonSubTypes.Type(value = Recipe::class, name = "Recipe")
)
abstract class Ingredient : BaseEntity, Identified, Labeled {
    var name: String? = null

    @ElementCollection
    private val labels: MutableSet<LabelRef> = HashSet()

    constructor() {}
    internal constructor(name: String?) {
        this.name = name
    }

    override fun getLabels(): Set<Label> {
        val s: MutableSet<Label> = HashSet()
        for (ref in labels) {
            s.add(ref.label!!)
        }
        return s
    }

    override fun addLabel(label: Label) {
        labels.add(LabelRef(label))
    }

    override fun removeLabel(label: Label) {
        labels.remove(LabelRef(label))
    }

    override fun clearLabels() {
        labels.clear()
    }

    companion object {
        @JvmField
        var BY_NAME = java.util.Comparator { a: Ingredient?, b: Ingredient? ->
            if (a == null) return@Comparator if (b == null) 0 else 1
            if (b == null) return@Comparator -1
            a.name!!.compareTo(b.name!!, ignoreCase = true)
        }
    }
}
