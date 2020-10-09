package mealplanner.sqldelight

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.String

interface PlantableQueries : Transacter {
  fun <T : Any> selectAll(mapper: (
    day_of_week: String,
    breakfast: String,
    lunch: String,
    dinner: String
  ) -> T): Query<T>

  fun selectAll(): Query<Plan>

  fun <T : Any> selectMeals(mapper: (
    breakfast: String,
    dinner: String,
    lunch: String
  ) -> T): Query<T>

  fun selectMeals(): Query<SelectMeals>

  fun insert(
    day_of_week: String,
    breakfast: String,
    lunch: String,
    dinner: String
  )

  fun delete()
}
