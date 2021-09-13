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
            val u = units
            return u != null && "" != u && !u.trim { it <= ' ' }
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
            val q = quantity
            if (q != null) {
                val uom = if (hasUomId()) em.find(
                    UnitOfMeasure::class.java, uomId
                ) else (if (hasUnits()) UnitOfMeasure.ensure(
                    em,
                    units
                ) else null)
                ref.quantity = Quantity(q, uom)
            }
            ref.preparation = preparation
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
                val q = ref.quantity
                if (q != null) {
                    info.quantity = q.quantity
                    val u = q.units
                    if (u != null) {
                        info.uomId = u.id
                        info.units = u.name
                    }
                }
                val ing = ref.ingredient
                if (ing != null) {
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
        val ings = ingredients
        if (ings != null) {
            r.ingredients = ings
                .stream()
                .map { ref: Ref -> ref.asIngredientRef(em) }
                .collect(Collectors.toList())
        }
        val photoFocus = photoFocus
        if (photoFocus != null && photoFocus.size == 2) {
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
            info.ownerId = r.owner?.id
            return info
        }

        @JvmStatic
        fun from(it: AggregateIngredient): IngredientInfo {
            val info = from(it as Ingredient)
            val ings = it.ingredients
            if (ings != null) {
                info.ingredients = ings
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
