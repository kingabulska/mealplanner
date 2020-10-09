package mealplanner

enum class Category ( val category: String){
    BREAKFAST("breakfast"),
    LUNCH("lunch"),
    DINNER("dinner");

    companion object {
        fun fromString(string: String): Category {
            return values().first() {it.category == string}
        }
    }

}
