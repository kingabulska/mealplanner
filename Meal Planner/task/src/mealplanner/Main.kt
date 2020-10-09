package mealplanner

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import mealplanner.sqldelight.Categories
import mealplanner.sqldelight.CategoriestableQueries
import mealplanner.sqldelight.IngredientstableQueries
import mealplanner.sqldelight.PlantableQueries
import java.io.File
import java.time.DayOfWeek

fun main(args: Array<String>) {

    val databaseName= "jdbc:sqlite:" + args[0]

    val driver: SqlDriver = JdbcSqliteDriver(databaseName)
    driver.use {
        Database.Schema.create(driver)

        val database = Database(
                driver = driver,
                categoriesAdapter = Categories.Adapter(
                        categoryAdapter = EnumColumnAdapter()
                )
        )

        val ask = "What would you like to do (add, show, plan, save, exit)?"

        println(ask)
        var input = readLine()

        while (input != "exit") {
            if (input == "add") {

                add(database)

            } else if (input == "show") {
                show(database)
            } else if (input == "plan") {
                plan(database)
            } else if (input == "save") {
                save(database)
            }

            println(ask)
            input = readLine()!!
        }
        println("Bye!")
    }
}

fun add (database: Database) {
    println("Which meal do you want to add (breakfast, lunch, dinner)?")
    var mealCat = readLine()!!

    while (mealCat != "breakfast" && mealCat != "dinner" && mealCat != "lunch") {
        println("Wrong meal category! Choose from: breakfast, lunch, dinner.")
        mealCat = readLine()!!
    }

    println("Meal's name:")
    var mealName = readLine()!!

    while (mealName.contains("""\d""".toRegex())) {
        println("Wrong format. Use letters only!")
        println("Meal's name:")
        mealName = readLine()!!
    }

    println("Ingredients:")

    var ingredients = readLine()!!

    while (ingredients.contains("""\d""".toRegex())) {
        println("Wrong format. Use letters only!")
        println("Ingredients:")
        ingredients = readLine()!!

    }

    val ingredientsList  = ingredients.split(", ").map { Ingredient(it) }.toMutableList()

    val categoriestableQueries: CategoriestableQueries = database.categoriestableQueries
    val ingredientstableQueries: IngredientstableQueries = database.ingredientstableQueries

    val mealCategory: Category = Category.fromString(mealCat)

    val meal1: Meal = Meal(mealName, mealCategory, ingredientsList)

    categoriestableQueries.insert(meal1.category, meal1.name)
    val idMeal = categoriestableQueries.sellectLastRowId().executeAsOne()

    for ( ingredient in ingredientsList) {
        ingredientstableQueries.insert(ingredient.name, idMeal)
    }

    println("Meal added!")
}

fun show (database: Database) {

    println("Which category do you want to print? (breakfast, lunch, dinner)?")
    var input = readLine()!!
    while (input != "lunch" && input != "breakfast" && input != "dinner") {
        println("Wrong meal category! Choose from: breakfast, lunch, dinner.")
        input = readLine()!!
    }

    val category: Category = Category.fromString(input)

    val categoriestableQueries: CategoriestableQueries = database.categoriestableQueries

    val sellectByCat = categoriestableQueries.sellectByCategory(category).executeAsList()

    val ingredientstableQueries: IngredientstableQueries = database.ingredientstableQueries

    if (sellectByCat.isEmpty()) {
        println("No meal found.")
    } else {
        for (row in sellectByCat) {
            val list = row
            val mealCat = list.category
            val name = list.meal_name
            val idMeal = list.id_meal
            val sellectIngred = ingredientstableQueries.selectAllIngredientForOneMeal(idMeal).executeAsList()

            println("Category: $mealCat")
            println("Name: $name")
            println("Ingredients:")
            sellectIngred.forEach { println(it) }
            println()
        }
    }
}

fun plan (database: Database) {
    val weekDayOfWeek = DayOfWeek.values()
    val plantableQueries: PlantableQueries = database.plantableQueries

    plantableQueries.delete()

    for (day in weekDayOfWeek) {

        val wrong = "This meal doesn’t exist. Choose a meal from the list above."

        println(day)
        var category: Category = Category.BREAKFAST

        val choose = "Choose ${category.name.toString()} for $day from the list above:"

        val brekfastsList =  printByCatAlfabet(database, category )
        println("Choose ${category.name.toString()} for $day from the list above:")
        var breakfast = readLine()!!
        while (!brekfastsList.contains(breakfast)){
            println(wrong)
            breakfast = readLine()!!
        }

        category = Category.LUNCH
        val lunchList = printByCatAlfabet(database, category)
        println("Choose ${category.name.toString()} for $day from the list above:")
        var lunch = readLine()!!
        while (!lunchList.contains(lunch)){
            println(wrong)
            lunch = readLine()!!
        }

        category = Category.DINNER
        val dinnerList = printByCatAlfabet(database, category)
        println("Choose ${category.name.toString()} for $day from the list above:")
        var dinner = readLine()!!
        while (!dinnerList.contains(dinner)){
            println(wrong)
            dinner = readLine()!!
        }

        plantableQueries.insert(day.toString(), breakfast, lunch, dinner)
        println("Yeah! We planned the meals for $day.")

    }

    printPlan(database)

}

fun save (database: Database) {

    var ingredients = mutableListOf<String>()
    println("Write a filename:")
    val inputFile = readLine()!!
    val file = File(inputFile)

    val plantableQueries: PlantableQueries = database.plantableQueries
    val categoriestableQueries: CategoriestableQueries = database.categoriestableQueries
    val ingredientstableQueries: IngredientstableQueries = database.ingredientstableQueries


    val breakfasts = plantableQueries.selectMeals().executeAsList().map { it.breakfast }
    val lunches = plantableQueries.selectMeals().executeAsList().map { it.lunch }
    val dinners = plantableQueries.selectMeals().executeAsList().map {it.dinner}

    if (breakfasts.isEmpty()) {
        println("Save isn't allowed. Plan your meals first.")
    } else {
        ingredients = addToIngredientList(database, breakfasts, ingredients)
        ingredients = addToIngredientList(database, lunches, ingredients)
        ingredients = addToIngredientList(database, dinners, ingredients)

        val ingredientsWithAmount = mutableMapOf<String, Int>()

        for (i in ingredients){
            if (!ingredientsWithAmount.containsKey(i)) {
                var counter = 0
                for (j in ingredients) {
                    if (i == j) {
                        counter ++
                    }
                }

                ingredientsWithAmount.put(i, counter)
            }
        }

        saveToFile(file, ingredientsWithAmount)
        println("Saved correctly!")
    }
}
fun saveToFile (file: File, ingredients: MutableMap<String, Int>) {

    for (i in ingredients) {
        var line = ""
        if (i.value > 1) {
            line = "${i.key} x${i.value}\n"
        }else {
            line = "${i.key}\n"
        }

        file.appendText(line)
    }

}
fun addToIngredientList(database: Database, mealsByCat: List<String>, ingredients: MutableList<String>): MutableList<String> {
    val categoriestableQueries: CategoriestableQueries = database.categoriestableQueries
    val ingredientstableQueries: IngredientstableQueries = database.ingredientstableQueries

    for (meal in mealsByCat) {
        val id = categoriestableQueries.selectIdByName(meal).executeAsOne()
        val ingredientsNames = ingredientstableQueries.selectAllIngredientForOneMeal(id).executeAsList().map { it.toString() }

        for (ingr in ingredientsNames) {
            ingredients.add(ingr)
        }

    }

    return ingredients
}
fun printByCatAlfabet (database: Database, category: Category): List<String> {

    val categoriestableQueries: CategoriestableQueries = database.categoriestableQueries

    val sellectByCat = categoriestableQueries.sellectByCategory(category).executeAsList().sortedBy { it.meal_name }.map { it.meal_name }

    sellectByCat.forEach { println(it) }

    return sellectByCat
}

fun printPlan (database: Database) {
    val plantableQueries: PlantableQueries = database.plantableQueries

    val sellectAll = plantableQueries.selectAll().executeAsList()

    for (day in sellectAll) {
        println(day.day_of_week)
        println("Breakfast: ${day.breakfast}")
        println("Lunch: ${day.lunch}")
        println("Dinner: ${day.dinner}")
        println()
    }
}