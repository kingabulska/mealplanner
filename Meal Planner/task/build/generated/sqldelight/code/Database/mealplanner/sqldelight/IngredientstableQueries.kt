package mealplanner.sqldelight

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Long
import kotlin.String

interface IngredientstableQueries : Transacter {
  fun selectAllIngredientForOneMeal(id_meal: Long): Query<String>

  fun insert(ingredient_name: String, id_meal: Long)
}
