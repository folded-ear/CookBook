package com.brennaswitzer.cookbook.domain

interface AggregateIngredient : Identified {
    val name: String?

    val ingredients: Collection<IngredientRef>?

    fun addIngredient(ingredient: Ingredient) {
        addIngredient(Quantity.ONE, ingredient, null)
    }

    fun addIngredient(quantity: Quantity, ingredient: Ingredient) {
        addIngredient(quantity, ingredient, null)
    }

    fun addIngredient(ingredient: Ingredient, preparation: String) {
        addIngredient(Quantity.ONE, ingredient, preparation)
    }

    fun addIngredient(
        quantity: Quantity?,
        ingredient: Ingredient,
        preparation: String?
    )

    /**
     * I return the PantryItem IngredientRefs for this Ingredient, including
     * those referenced recursively through nested `AggregateIngredient`s.
     */
    fun assemblePantryItemRefs(): Collection<IngredientRef>

    /**
     * I return all the "raw" IngredientRefs for this Ingredient, including
     * those referenced recursively through nested `AggregateIngredient`s.
     */
    fun assembleRawIngredientRefs(): Collection<IngredientRef>
}
