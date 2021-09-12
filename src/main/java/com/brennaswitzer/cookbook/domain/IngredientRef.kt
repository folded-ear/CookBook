package com.brennaswitzer.cookbook.domain

import com.brennaswitzer.cookbook.domain.Ingredient
import javax.persistence.*

@Embeddable
// it's @Embeddable, and IntelliJ's too dumb
class IngredientRef : MutableItem {
    @Column(name = "_order")
    private var _idx = 0
    private var raw: String? = null

    @Embedded
    private var quantity: Quantity? = null
    private var preparation: String? = null

    @ManyToOne(targetEntity = Ingredient::class, cascade = [CascadeType.MERGE])
    private var ingredient: Ingredient? = null

    constructor() {}
    constructor(ingredient: Ingredient) : this(null, ingredient, null) {}
    constructor(
        quantity: Quantity?,
        ingredient: Ingredient,
        preparation: String?
    ) {
        setQuantity(quantity!!)
        setIngredient(ingredient)
        setPreparation(preparation!!)
    }

    constructor(raw: String?) {
        setRaw(raw)
    }

    fun hasIngredient(): Boolean {
        return ingredient != null
    }

    override fun getRaw(): String {
        return if (raw == null) toString() else raw!!
    }

    override fun getQuantity(): Quantity {
        return quantity ?: Quantity.ONE
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

    fun get_idx(): Int {
        return _idx
    }

    override fun getPreparation(): String {
        return preparation!!
    }

    override fun getIngredient(): Ingredient {
        return ingredient!!
    }

    fun set_idx(_idx: Int) {
        this._idx = _idx
    }

    fun setRaw(raw: String?) {
        this.raw = raw
    }

    override fun setQuantity(quantity: Quantity) {
        this.quantity = quantity
    }

    override fun setPreparation(preparation: String) {
        this.preparation = preparation
    }

    override fun setIngredient(ingredient: Ingredient) {
        this.ingredient = ingredient
    }

    companion object {
        @JvmField
        var BY_INGREDIENT_NAME =
            java.util.Comparator { a: IngredientRef, b: IngredientRef ->
                val an = a.ingredient?.name ?: a.getRaw()
                val bn = b.ingredient?.name ?: b.getRaw()
                an.compareTo(bn, ignoreCase = true)
            }
    }
}
