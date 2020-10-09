package mealplanner.sqldelight

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Long
import kotlin.String
import mealplanner.Category

interface CategoriestableQueries : Transacter {
  fun <T : Any> selectAll(mapper: (
    category: Category,
    meal_name: String,
    id_meal: Long
  ) -> T): Query<T>

  fun selectAll(): Query<Categories>

  fun sellectLastRowId(): Query<Long>

  fun <T : Any> sellectByCategory(category: Category, mapper: (
    category: Category,
    meal_name: String,
    id_meal: Long
  ) -> T): Query<T>

  fun sellectByCategory(category: Category): Query<Categories>

  fun selectIdByName(meal_name: String): Query<Long>

  fun insert(category: Category, meal_name: String)
}
