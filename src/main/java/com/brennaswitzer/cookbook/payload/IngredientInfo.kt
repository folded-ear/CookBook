package com.brennaswitzer.cookbook.payload

import com.brennaswitzer.cookbook.domain.*
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*
import java.util.stream.Collectors
import javax.persistence.EntityManager

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class IngredientInfo {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    class Ref {
        var raw: String? = null
        var quantity: Double? = null

        @get:Deprecated("")
        @set:Deprecated("")
        var units: String? = null
        var uomId: Long? = null
        var ingredient: String? = null
        var ingredientId: Long? = null
        var preparation: String? = null
        fun hasQuantity(): Boolean {
            return quantity != null
        }

        @Deprecated("")
        fun hasUnits(): Boolean {
            return units != null && "" != units && !units!!.trim { it <= ' ' }
                .isEmpty()
        }

        fun hasUomId(): Boolean {
            return uomId != null
        }

        fun hasIngredient(): Boolean {
            return ingredient != null
        }

        fun hasIngredientId(): Boolean {
            return ingredientId != null
        }

        fun asIngredientRef(em: EntityManager): IngredientRef {
            val ref = IngredientRef()
            ref.raw = raw
            if (hasQuantity()) {
                val uom = if (hasUomId()) em.find(
                    UnitOfMeasure::class.java, uomId
                ) else (if (hasUnits()) UnitOfMeasure.ensure(
                    em,
                    units
                ) else null)
                ref.quantity = Quantity(quantity!!, uom)
            }
            ref.preparation = preparation!!
            if (hasIngredientId()) {
                ref.ingredient = em.find(Ingredient::class.java, ingredientId)
            } else if (hasIngredient()) {
                val it = PantryItem(ingredient)
                em.persist(it)
                ref.ingredient = it
            }
            return ref
        }

        companion object {
            @JvmStatic
            fun from(ref: IngredientRef): Ref {
                val info = Ref()
                info.raw = ref.raw
                if (ref.hasQuantity()) {
                    val q = ref.quantity!!
                    info.quantity = q.quantity
                    if (q.hasUnits()) {
                        info.uomId = q.units!!.id
                        info.units = q.units!!.name
                    }
                }
                if (ref.hasIngredient()) {
                    val ing = ref.ingredient!!
                    info.ingredientId = ing.id
                    info.ingredient = ing.name
                }
                info.preparation = ref.preparation
                return info
            }
        }
    }

    var id: Long? = null
    var type: String? = null
    var name: String? = null
    var storeOrder: Int? = null
    var externalUrl: String? = null
    var directions: String? = null
    var ingredients: List<Ref>? = null
    var labels: List<String?>? = null
        get() = field ?: Collections.emptyList()
    var ownerId: Long? = null
    var yield: Int? = null
    var calories: Int? = null
    var totalTime: Int? = null
    var photo: String? = null
    var photoFocus: FloatArray? = null
    var cookThis: Boolean? = null

    fun isCookThis(): Boolean {
        return cookThis != null && cookThis as Boolean
    }

    fun asRecipe(em: EntityManager): Recipe {
        val r = if (id == null) Recipe() else em.find(
            Recipe::class.java, id
        )
        r.name = name
        r.externalUrl = externalUrl
        r.directions = directions
        r.yield = `yield`
        r.totalTime = totalTime
        r.calories = calories
        if (ingredients != null) {
            r.ingredients = ingredients!!
                .stream()
                .map { ref: Ref -> ref.asIngredientRef(em) }
                .collect(Collectors.toList())
        }
        if (photoFocus != null && photoFocus!!.size == 2) {
            r.getPhoto(true).focusArray = photoFocus
        }
        return r
    }

    companion object {
        @JvmStatic
        fun from(r: Recipe): IngredientInfo {
            val info = from(r as AggregateIngredient)
            info.type = "Recipe"
            info.externalUrl = r.externalUrl
            info.directions = r.directions
            info.yield = r.yield
            info.totalTime = r.totalTime
            info.calories = r.calories
            if (r.owner != null) {
                info.ownerId = r.owner!!.id
            }
            return info
        }

        @JvmStatic
        fun from(it: AggregateIngredient): IngredientInfo {
            val info = from(it as Ingredient)
            if (it.ingredients != null) {
                info.ingredients = it.ingredients!!
                    .stream()
                    .map { ref: IngredientRef -> Ref.from(ref) }
                    .collect(Collectors.toList())
            }
            return info
        }

        @JvmStatic
        fun from(it: Ingredient): IngredientInfo {
            val info = IngredientInfo()
            info.id = it.id
            info.name = it.name
            if (it.hasLabels()) {
                info.labels = it.labels
                    .stream()
                    .map(Label::name)
                    .collect(Collectors.toList())
            }
            return info
        }

        @JvmStatic
        fun from(it: PantryItem): IngredientInfo {
            val info = from(it as Ingredient)
            info.type = "PantryItem"
            info.storeOrder = it.storeOrder
            return info
        }
    }
}
