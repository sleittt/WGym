package com.example.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.presentation.HomeScreen
import com.example.presentation.auth.screens.AuthCheckScreen
import com.example.presentation.auth.screens.RegisterScreen
import com.example.presentation.meal.screens.AddFoodItemScreen
import com.example.presentation.meal.screens.FoodItemsScreen
import com.example.presentation.meal.screens.MealItemDetailScreen
import com.example.presentation.meal.screens.NutritionScreen
import com.example.presentation.stats.screen.StatisticsScreen
import com.example.presentation.stats.screen.BodyMeasurementsScreen
import com.example.presentation.stats.screen.NutritionStatsScreen
import com.example.presentation.stats.screen.WorkoutHistoryScreen
import com.example.presentation.stats.screen.WorkoutTemplateHistoryScreen
import com.example.presentation.workout.screens.ActiveWorkoutScreen
import com.example.presentation.workout.screens.ExerciseCreateScreen
import com.example.presentation.workout.screens.ExerciseDetailScreen
import com.example.presentation.workout.screens.ExerciseTemplatesScreen
import com.example.presentation.workout.screens.WorkoutTemplateDetailScreen
import com.example.presentation.workout.screens.WorkoutTemplatesScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.AuthCheck.route,
            modifier = Modifier.fillMaxSize()
        ) {
            // === AUTH ===
            composable(Screen.AuthCheck.route) {
                AuthCheckScreen(navController = navController)
            }
            composable(Screen.Register.route) {
                RegisterScreen(navController = navController)
            }

            // === HOME ===
            composable(Screen.Home.route) { HomeScreen(navController) }

            // === WORKOUT ===
            composable(Screen.WorkoutTemplates.route) {
                WorkoutTemplatesScreen(navController)
            }
            composable(
                route = Screen.WorkoutTemplateDetail.route,
                arguments = listOf(navArgument("templateId") { type = NavType.StringType })
            ) { backStackEntry ->
                WorkoutTemplateDetailScreen(
                    navController = navController,
                    templateId = backStackEntry.arguments?.getString("templateId") ?: "0"
                )
            }
            composable(
                route = Screen.ActiveWorkout.route,
                arguments = listOf(navArgument("templateId") { type = NavType.StringType })
            ) {
                ActiveWorkoutScreen(navController = navController)
            }
            composable(
                route = Screen.ExerciseTemplates.route,
                arguments = listOf(
                    navArgument("selectMode") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->
                val selectMode = backStackEntry.arguments?.getBoolean("selectMode") ?: false
                ExerciseTemplatesScreen(
                    navController = navController,
                    selectMode = selectMode
                )
            }
            composable(
                route = Screen.ExerciseDetail.route,
                arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
            ) { backStackEntry ->
                ExerciseDetailScreen(
                    navController = navController,
                    exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
                )
            }
            composable(Screen.ExerciseCreate.route) {
                ExerciseCreateScreen(navController)
            }

            // ===== NUTRITION SCREENS =====
            composable(
                route = Screen.Nutrition.route,
                arguments = listOf(
                    navArgument("date") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                NutritionScreen(
                    navController = navController,
                    dateString = backStackEntry.arguments?.getString("date")
                )
            }
            composable(Screen.FoodItems.route) {
                FoodItemsScreen(navController = navController)
            }
            composable(Screen.AddFoodItem.route) {
                AddFoodItemScreen(navController = navController)
            }

            // ===== STATISTICS SCREENS =====
            composable(Screen.Statistics.route) {
                StatisticsScreen(navController = navController)
            }
            composable(Screen.WorkoutHistory.route) {
                WorkoutHistoryScreen(navController = navController)
            }
            composable(
                route = Screen.WorkoutTemplateHistory.route,
                arguments = listOf(navArgument("templateId") { type = NavType.StringType })
            ) { backStackEntry ->
                WorkoutTemplateHistoryScreen(
                    navController = navController,
                    templateId = backStackEntry.arguments?.getString("templateId") ?: "0"
                )
            }
            composable(Screen.NutritionStats.route) {
                NutritionStatsScreen(navController = navController)
            }
            composable(
                route = Screen.BodyMeasurements.route,
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) { backStackEntry ->
                BodyMeasurementsScreen(
                    navController = navController,
                    measurementType = backStackEntry.arguments?.getString("type") ?: "weight"
                )
            }

            composable(Screen.Settings.route) { /* SettingsScreen(navController) */ }
            composable(Screen.MealItemDetail.route) {
                MealItemDetailScreen(navController = navController)
            }
        }
    }
}
