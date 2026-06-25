package com.example.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object WorkoutTemplates : Screen("workout_templates")
    data object WorkoutTemplateDetail : Screen("workout_template_detail/{templateId}") {
        fun createRoute(templateId: String) = "workout_template_detail/$templateId"
    }
    data object ActiveWorkout : Screen("active_workout/{templateId}") {
        fun createRoute(templateId: String) = "active_workout/$templateId"
    }
    data object ExerciseTemplates : Screen("exercise_templates/{selectMode}") {
        fun createRoute(selectMode: Boolean = false) = "exercise_templates/$selectMode"
    }
    data object ExerciseDetail : Screen("exercise_detail/{exerciseId}") {
        fun createRoute(exerciseId: String) = "exercise_detail/$exerciseId"
    }
    data object Nutrition : Screen("nutrition/{date}") {
        fun createRoute(date: String = "") = if (date.isNotEmpty()) "nutrition/$date" else "nutrition/"
    }
    data object FoodItems : Screen("food_items")
    data object AddFoodItem : Screen("add_food_item")

    // Statistics
    data object Statistics : Screen("statistics")
    data object WorkoutHistory : Screen("workout_history")
    data object NutritionStats : Screen("nutrition_stats")
    data object WorkoutTemplateHistory : Screen("workout_template_history/{templateId}") {
        fun createRoute(templateId: String) = "workout_template_history/$templateId"
    }
    data object BodyMeasurements : Screen("body_measurements/{type}") {
        fun createRoute(type: String) = "body_measurements/$type"
    }

    data object Settings : Screen("settings")
    data object ExerciseCreate : Screen("exercise_create")
    data object MealItemDetail : Screen("meal_item_detail")
}
