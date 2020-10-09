package mealplanner

import mealplanner.Category
import mealplanner.Ingredient

class Meal(
        val name: String,
        val category: Category,
        val ingredients: MutableList<Ingredient>
) {
}