import org.hyperskill.hstest.dynamic.input.DynamicTestingMethod
import org.hyperskill.hstest.testcase.CheckResult
import org.hyperskill.hstest.testing.TestedProgram
import java.io.File

class MealPlannerTestS6() : KotlinTest<String>() {

    private val dbName = "test.db"

    @DynamicTestingMethod
    open fun test(): CheckResult? {
        val main = TestedProgram(classUnderTest)

        main.start(dbName)
        main.execute("show")
        val output = main.execute("lunch")


        if (output != "No meal found.\n" +
                "What would you like to do (add, show, plan, save, exit)?\n") {
            return CheckResult.wrong("Wrong answer on  \"show\" command.")
        }
        return CheckResult.correct()
    }

    @DynamicTestingMethod
    open fun test2(): CheckResult? {
        val main = TestedProgram(classUnderTest)

        main.start(dbName)
        main.execute("save")
        val output = main.execute("testFile.txt")


        if (output != "Save isn't allowed. Plan your meals first.\n" +
                "What would you like to do (add, show, plan, save, exit)?\n") {
            return CheckResult.wrong("Wrong answer on  \"save\" command.")
        }
        return CheckResult.correct()
    }

    @DynamicTestingMethod
    open fun test3(): CheckResult? {
        val main = TestedProgram(classUnderTest)

        main.start(dbName)
        main.execute("add")
        main.execute("breakfast")
        main.execute("cat")
        main.execute("bread, peanut butter, jelly")

        main.execute("add")
        main.execute("breakfast")
        main.execute("ara")
        main.execute("tomato, cheese, bread")

        main.execute("add")
        main.execute("breakfast")
        main.execute("zebra")
        main.execute("muesli, milk, nuts")

        main.execute("add")
        main.execute("lunch")
        main.execute("dog")
        main.execute("ketchup, sausage")

        main.execute("add")
        main.execute("lunch")
        main.execute("mouse")
        main.execute("fish, potato")

        main.execute("add")
        main.execute("dinner")
        main.execute("lion")
        main.execute("carmel, nuts, cream")

        val output = main.execute("plan").split("\n")
        if (output[1] != "ara" && output[2] != "cat" && output[3] != "zebra") {
            return CheckResult.wrong("List of meals not sorted.")
        }

        main.execute("ara")
        main.execute("dog")
        val output2 = main.execute("lion").toLowerCase()
        if (!output2.contains("yeah! we planned the meals for monday.\n")) {
            return CheckResult.wrong("Wrong answer on \"plan\" command.")
        }

        val output3 = main.execute("parrot")
        if (output3 != "This meal doesn’t exist. Choose a meal from the list above.\n") {
            return CheckResult.wrong("Wrong answer on \"plan\" command.")
        }
        repeat(5) {
            main.execute("zebra")
            main.execute("mouse")
            main.execute("lion")
        }
        main.execute("zebra")
        main.execute("mouse")
        val output4 = main.execute("lion").split("\n")

        if (output[0].toLowerCase() != "yeah! we planned the meals for sunday." &&
                output4[1].toLowerCase() != "monday" && output4[2].toLowerCase() != "breakfast: ara"
                && output4[3].toLowerCase() != "lunch: dog" && output4[4].toLowerCase() != "dinner: lion") {
            return CheckResult.wrong("Wrong answer on \"plan\" command.")
        }

        main.execute("save")
        val filename = "testFile.txt"
        val output5 = main.execute(filename)

        if (!output5.contains("Saved correctly!")) {
            return CheckResult.wrong("Wrong answer on \"save\" command.")
        }
        val userFile = File(filename)
        val shoppingList =  userFile.readText().split("\n")

        if (!shoppingList.contains("muesli x6")) {
            return CheckResult.wrong("Wrong shopping list format.")
        }

        if (!shoppingList.contains("tomato")) {
            return CheckResult.wrong("Wrong shopping list format.")
        }

        main.execute("exit")
        main.stop()

        val file = File(dbName)
        file.delete()

        userFile.delete()

        return CheckResult.correct()
    }
}