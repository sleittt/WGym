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
import com.example.presentation.meal.screens.FoodItemsScreen
import com.example.presentation.meal.screens.NutritionScreen
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
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }

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
            ) { backStackEntry ->
                ActiveWorkoutScreen(
                    navController = navController,
                    templateId = backStackEntry.arguments?.getString("templateId") ?: "0"
                )
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
            composable(Screen.Nutrition.route) {
                NutritionScreen(navController = navController)
            }

            composable(
                route = Screen.FoodItems.route,
                arguments = listOf(
                    navArgument("selectMode") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                    navArgument("mealType") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("date") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val selectMode = backStackEntry.arguments?.getBoolean("selectMode") ?: false
                val mealType = backStackEntry.arguments?.getString("mealType") ?: ""
                val date = backStackEntry.arguments?.getString("date") ?: ""
                FoodItemsScreen(
                    navController = navController,
                    selectMode = selectMode,
                    mealType = mealType,
                    date = date
                )
            }

            composable(Screen.Statistics.route) { /* StatisticsScreen(navController) */ }
            composable(Screen.Settings.route) { /* SettingsScreen(navController) */ }
        }
    }
}
