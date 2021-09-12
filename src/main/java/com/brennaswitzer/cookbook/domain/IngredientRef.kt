package com.brennaswitzer.cookbook.domain

import com.brennaswitzer.cookbook.domain.Ingredient
import javax.persistence.*

@Embeddable
// it's @Embeddable, and IntelliJ's too dumb
class IngredientRef : MutableItem {
    @Column(name = "_order")
    var _idx = 0
    override var raw: String? = null
        get() = field ?: toString()

    @Embedded
    override var quantity: Quantity? = null
        get() = field ?: Quantity.ONE

    override var preparation: String? = null

    @ManyToOne(targetEntity = Ingredient::class, cascade = [CascadeType.MERGE])
    override var ingredient: Ingredient? = null

    constructor() {}
    constructor(ingredient: Ingredient) : this(null, ingredient, null) {}
    constructor(
        quantity: Quantity?,
        ingredient: Ingredient,
        preparation: String?
    ) {
        this.quantity = quantity
        this.ingredient = ingredient
        this.preparation = preparation
    }

    constructor(raw: String?) {
        this.raw = raw
    }

    fun hasIngredient(): Boolean {
        return ingredient != null
    }

    fun hasQuantity(): Boolean {
        return quantity != null
    }

    fun hasPreparation(): Boolean {
        return preparation != null && !preparation!!.isEmpty()
    }

    override fun toString(): String {
        return toString(true)!!
    }

    fun toString(includePrep: Boolean): String? {
        if (!hasIngredient()) return raw
        val sb = StringBuilder()
        if (hasQuantity()) {
            sb.append(quantity).append(' ')
        }
        sb.append(ingredient!!.name)
        if (includePrep && hasPreparation()) {
            sb.append(", ").append(preparation)
        }
        return sb.toString()
    }

    companion object {
        @JvmField
        var BY_INGREDIENT_NAME =
            java.util.Comparator { a: IngredientRef, b: IngredientRef ->
                val an = a.ingredient?.name ?: a.raw!!
                val bn = b.ingredient?.name ?: b.raw!!
                an.compareTo(bn, ignoreCase = true)
            }
    }
}
