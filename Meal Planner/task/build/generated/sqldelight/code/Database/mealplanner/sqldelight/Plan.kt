package mealplanner.sqldelight

import kotlin.String

data class Plan(
  val day_of_week: String,
  val breakfast: String,
  val lunch: String,
  val dinner: String
) {
  override fun toString(): String = """
  |Plan [
  |  day_of_week: $day_of_week
  |  breakfast: $breakfast
  |  lunch: $lunch
  |  dinner: $dinner
  |]
  """.trimMargin()
}
