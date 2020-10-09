package mealplanner.sqldelight

import com.squareup.sqldelight.ColumnAdapter
import kotlin.Long
import kotlin.String
import mealplanner.Category

data class Categories(
  val category: Category,
  val meal_name: String,
  val id_meal: Long
) {
  override fun toString(): String = """
  |Categories [
  |  category: $category
  |  meal_name: $meal_name
  |  id_meal: $id_meal
  |]
  """.trimMargin()

  class Adapter(
    val categoryAdapter: ColumnAdapter<Category, String>
  )
}
