package mealplanner

import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.db.SqlDriver
import mealplanner.MealPlannertask.newInstance
import mealplanner.MealPlannertask.schema
import mealplanner.sqldelight.Categories
import mealplanner.sqldelight.CategoriestableQueries
import mealplanner.sqldelight.IngredientstableQueries
import mealplanner.sqldelight.PlantableQueries

interface Database : Transacter {
  val categoriestableQueries: CategoriestableQueries

  val ingredientstableQueries: IngredientstableQueries

  val plantableQueries: PlantableQueries

  companion object {
    val Schema: SqlDriver.Schema
      get() = Database::class.schema

    operator fun invoke(driver: SqlDriver, categoriesAdapter: Categories.Adapter): Database =
        Database::class.newInstance(driver, categoriesAdapter)}
}
