package mealplanner.sqldelight

import kotlin.String

data class SelectMeals(
  val breakfast: String,
  val dinner: String,
  val lunch: String
) {
  override fun toString(): String = """
  |SelectMeals [
  |  breakfast: $breakfast
  |  dinner: $dinner
  |  lunch: $lunch
  |]
  """.trimMargin()
}
