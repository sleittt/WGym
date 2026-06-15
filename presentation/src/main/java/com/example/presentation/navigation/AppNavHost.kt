package com.example.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.presentation.meal.screens.AddFoodItemScreen
import com.example.presentation.meal.screens.AddFoodToMealScreen
import com.example.presentation.meal.screens.FoodItemsScreen
import com.example.presentation.meal.screens.MealsScreen
import com.example.presentation.ui.screen.*
import com.example.presentation.workout.screens.ActiveWorkoutScreen
import com.example.presentation.workout.screens.AddExerciseToWorkoutScreen
import com.example.presentation.workout.screens.ExerciseDetailScreen
import com.example.presentation.workout.screens.ExerciseTemplatesScreen
import com.example.presentation.workout.screens.WorkoutTemplateDetailScreen
import com.example.presentation.workout.screens.WorkoutTemplatesScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = modifier.padding(padding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.WorkoutTemplates.route) { WorkoutTemplatesScreen(navController) }
            composable(
                Screen.WorkoutTemplateDetail.route,
                arguments = listOf(navArgument("templateId") { type = NavType.StringType })
            ) { WorkoutTemplateDetailScreen(navController) }
            composable(
                Screen.ActiveWorkout.route,
                arguments = listOf(navArgument("templateId") { type = NavType.StringType })
            ) { ActiveWorkoutScreen(navController) }
            composable(Screen.ExerciseTemplates.route) { ExerciseTemplatesScreen(navController) }
            composable(
                Screen.ExerciseDetail.route,
                arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
            ) { ExerciseDetailScreen(navController) }
            composable(Screen.AddExerciseToWorkout.route) { AddExerciseToWorkoutScreen(navController) }
            composable(Screen.Nutrition.route) { MealsScreen(navController) }
            composable(Screen.FoodItems.route) { FoodItemsScreen(navController) }
            composable(Screen.AddFoodItem.route) { AddFoodItemScreen(navController) }
            composable(Screen.AddFoodToMeal.route) { AddFoodToMealScreen(navController) }
            //composable(Screen.WorkoutHistory.route) { WorkoutHistoryScreen(navController) }
            composable(Screen.Statistics.route) { StatisticsScreen(navController) }
            //composable(Screen.WeightDetail.route) { WeightDetailScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
        }
    }
}
