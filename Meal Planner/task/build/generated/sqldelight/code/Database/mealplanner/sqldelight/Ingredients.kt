package mealplanner.sqldelight

import kotlin.Long
import kotlin.String

data class Ingredients(
  val ingredient_id: Long,
  val ingredient_name: String,
  val id_meal: Long
) {
  override fun toString(): String = """
  |Ingredients [
  |  ingredient_id: $ingredient_id
  |  ingredient_name: $ingredient_name
  |  id_meal: $id_meal
  |]
  """.trimMargin()
}
