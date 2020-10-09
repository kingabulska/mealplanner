package mealplanner.MealPlannertask

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.TransacterImpl
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.internal.copyOnWriteList
import kotlin.Any
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.MutableList
import kotlin.jvm.JvmField
import kotlin.reflect.KClass
import mealplanner.Category
import mealplanner.Database
import mealplanner.sqldelight.Categories
import mealplanner.sqldelight.CategoriestableQueries
import mealplanner.sqldelight.IngredientstableQueries
import mealplanner.sqldelight.Plan
import mealplanner.sqldelight.PlantableQueries
import mealplanner.sqldelight.SelectMeals

internal val KClass<Database>.schema: SqlDriver.Schema
  get() = DatabaseImpl.Schema

internal fun KClass<Database>.newInstance(driver: SqlDriver, categoriesAdapter: Categories.Adapter):
    Database = DatabaseImpl(driver, categoriesAdapter)

private class DatabaseImpl(
  driver: SqlDriver,
  internal val categoriesAdapter: Categories.Adapter
) : TransacterImpl(driver), Database {
  override val categoriestableQueries: CategoriestableQueriesImpl = CategoriestableQueriesImpl(this,
      driver)

  override val ingredientstableQueries: IngredientstableQueriesImpl =
      IngredientstableQueriesImpl(this, driver)

  override val plantableQueries: PlantableQueriesImpl = PlantableQueriesImpl(this, driver)

  object Schema : SqlDriver.Schema {
    override val version: Int
      get() = 1

    override fun create(driver: SqlDriver) {
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS categories (
          |    category TEXT NOT NULL,
          |    meal_name TEXT NOT NULL,
          |    id_meal INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS ingredients (
          |    ingredient_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    ingredient_name TEXT NOT NULL,
          |    id_meal INTEGER NOT NULL,
          |    CONSTRAINT fk_meal FOREIGN KEY (id_meal)
          |    REFERENCES categories(id_meal)
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS plan(
          |    day_of_week TEXT NOT NULL,
          |    breakfast TEXT NOT NULL,
          |    lunch TEXT NOT NULL,
          |    dinner TEXT NOT NULL
          |    )
          """.trimMargin(), 0)
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Int,
      newVersion: Int
    ) {
    }
  }
}

private class CategoriestableQueriesImpl(
  private val database: DatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), CategoriestableQueries {
  internal val selectAll: MutableList<Query<*>> = copyOnWriteList()

  internal val sellectLastRowId: MutableList<Query<*>> = copyOnWriteList()

  internal val sellectByCategory: MutableList<Query<*>> = copyOnWriteList()

  internal val selectIdByName: MutableList<Query<*>> = copyOnWriteList()

  override fun <T : Any> selectAll(mapper: (
    category: Category,
    meal_name: String,
    id_meal: Long
  ) -> T): Query<T> = Query(502466265, selectAll, driver, "categoriestable.sq", "selectAll", """
  |SELECT *
  |FROM categories
  """.trimMargin()) { cursor ->
    mapper(
      database.categoriesAdapter.categoryAdapter.decode(cursor.getString(0)!!),
      cursor.getString(1)!!,
      cursor.getLong(2)!!
    )
  }

  override fun selectAll(): Query<Categories> = selectAll { category, meal_name, id_meal ->
    mealplanner.sqldelight.Categories(
      category,
      meal_name,
      id_meal
    )
  }

  override fun sellectLastRowId(): Query<Long> = Query(450831015, sellectLastRowId, driver,
      "categoriestable.sq", "sellectLastRowId", "SELECT last_insert_rowid()") { cursor ->
    cursor.getLong(0)!!
  }

  override fun <T : Any> sellectByCategory(category: Category, mapper: (
    category: Category,
    meal_name: String,
    id_meal: Long
  ) -> T): Query<T> = SellectByCategoryQuery(category) { cursor ->
    mapper(
      database.categoriesAdapter.categoryAdapter.decode(cursor.getString(0)!!),
      cursor.getString(1)!!,
      cursor.getLong(2)!!
    )
  }

  override fun sellectByCategory(category: Category): Query<Categories> =
      sellectByCategory(category) { category, meal_name, id_meal ->
    mealplanner.sqldelight.Categories(
      category,
      meal_name,
      id_meal
    )
  }

  override fun selectIdByName(meal_name: String): Query<Long> = SelectIdByNameQuery(meal_name) {
      cursor ->
    cursor.getLong(0)!!
  }

  override fun insert(category: Category, meal_name: String) {
    driver.execute(1493518181, """
    |INSERT INTO categories (category, meal_name)
    |VALUES (?, ?)
    """.trimMargin(), 2) {
      bindString(1, database.categoriesAdapter.categoryAdapter.encode(category))
      bindString(2, meal_name)
    }
    notifyQueries(1493518181, {database.categoriestableQueries.selectAll +
        database.categoriestableQueries.sellectByCategory +
        database.categoriestableQueries.selectIdByName})
  }

  private inner class SellectByCategoryQuery<out T : Any>(
    @JvmField
    val category: Category,
    mapper: (SqlCursor) -> T
  ) : Query<T>(sellectByCategory, mapper) {
    override fun execute(): SqlCursor = driver.executeQuery(1857258605, """
    |SELECT *
    |FROM categories
    |WHERE category = ?
    """.trimMargin(), 1) {
      bindString(1, database.categoriesAdapter.categoryAdapter.encode(category))
    }

    override fun toString(): String = "categoriestable.sq:sellectByCategory"
  }

  private inner class SelectIdByNameQuery<out T : Any>(
    @JvmField
    val meal_name: String,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectIdByName, mapper) {
    override fun execute(): SqlCursor = driver.executeQuery(-1471781307, """
    |SELECT id_meal
    |FROM categories
    |WHERE meal_name = ?
    """.trimMargin(), 1) {
      bindString(1, meal_name)
    }

    override fun toString(): String = "categoriestable.sq:selectIdByName"
  }
}

private class IngredientstableQueriesImpl(
  private val database: DatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), IngredientstableQueries {
  internal val selectAllIngredientForOneMeal: MutableList<Query<*>> = copyOnWriteList()

  override fun selectAllIngredientForOneMeal(id_meal: Long): Query<String> =
      SelectAllIngredientForOneMealQuery(id_meal) { cursor ->
    cursor.getString(0)!!
  }

  override fun insert(ingredient_name: String, id_meal: Long) {
    driver.execute(1966886375, """
    |INSERT INTO ingredients (ingredient_name, id_meal)
    |VALUES (?, ?)
    """.trimMargin(), 2) {
      bindString(1, ingredient_name)
      bindLong(2, id_meal)
    }
    notifyQueries(1966886375, {database.ingredientstableQueries.selectAllIngredientForOneMeal})
  }

  private inner class SelectAllIngredientForOneMealQuery<out T : Any>(
    @JvmField
    val id_meal: Long,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectAllIngredientForOneMeal, mapper) {
    override fun execute(): SqlCursor = driver.executeQuery(2029613736, """
    |SELECT ingredient_name
    |FROM ingredients
    |WHERE id_meal = ?
    """.trimMargin(), 1) {
      bindLong(1, id_meal)
    }

    override fun toString(): String = "ingredientstable.sq:selectAllIngredientForOneMeal"
  }
}

private class PlantableQueriesImpl(
  private val database: DatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), PlantableQueries {
  internal val selectAll: MutableList<Query<*>> = copyOnWriteList()

  internal val selectMeals: MutableList<Query<*>> = copyOnWriteList()

  override fun <T : Any> selectAll(mapper: (
    day_of_week: String,
    breakfast: String,
    lunch: String,
    dinner: String
  ) -> T): Query<T> = Query(-226846298, selectAll, driver, "plantable.sq", "selectAll", """
  |SELECT *
  |FROM plan
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!
    )
  }

  override fun selectAll(): Query<Plan> = selectAll { day_of_week, breakfast, lunch, dinner ->
    mealplanner.sqldelight.Plan(
      day_of_week,
      breakfast,
      lunch,
      dinner
    )
  }

  override fun <T : Any> selectMeals(mapper: (
    breakfast: String,
    dinner: String,
    lunch: String
  ) -> T): Query<T> = Query(1054906325, selectMeals, driver, "plantable.sq", "selectMeals", """
  |SELECT breakfast, dinner, lunch
  |FROM plan
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!
    )
  }

  override fun selectMeals(): Query<SelectMeals> = selectMeals { breakfast, dinner, lunch ->
    mealplanner.sqldelight.SelectMeals(
      breakfast,
      dinner,
      lunch
    )
  }

  override fun insert(
    day_of_week: String,
    breakfast: String,
    lunch: String,
    dinner: String
  ) {
    driver.execute(-72192072, """
    |INSERT INTO plan (day_of_week, breakfast, lunch, dinner)
    |VALUES (?, ?, ?, ?)
    """.trimMargin(), 4) {
      bindString(1, day_of_week)
      bindString(2, breakfast)
      bindString(3, lunch)
      bindString(4, dinner)
    }
    notifyQueries(-72192072, {database.plantableQueries.selectAll +
        database.plantableQueries.selectMeals})
  }

  override fun delete() {
    driver.execute(-223858006, """DELETE FROM plan""", 0)
    notifyQueries(-223858006, {database.plantableQueries.selectAll +
        database.plantableQueries.selectMeals})
  }
}
