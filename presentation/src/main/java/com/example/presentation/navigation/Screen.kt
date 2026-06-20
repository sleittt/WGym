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
    data object AddExerciseToWorkout : Screen("add_exercise_to_workout")
    data object Nutrition : Screen("nutrition")
    data object FoodItems : Screen("food_items")
    data object AddFoodItem : Screen("add_food_item")
    data object AddFoodToMeal : Screen("add_food_to_meal")
    data object WorkoutHistory : Screen("workout_history")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")
    data object ExerciseSelect : Screen("exercise_select")

    data object ExerciseCreate : Screen("exercise_create")
}