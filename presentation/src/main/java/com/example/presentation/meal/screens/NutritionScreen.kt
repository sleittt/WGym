package com.example.presentation.meal.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.presentation.meal.viewmodels.NutritionViewModel
import com.example.presentation.navigation.Screen
import com.example.presentation.ui.components.BottomNavigationBar
import com.example.presentation.ui.components.DateSelector
import com.example.presentation.ui.components.ErrorState
import com.example.presentation.ui.components.LoadingIndicator
import com.example.presentation.ui.components.MealSectionCard
import com.example.presentation.ui.components.NutritionSummaryHeader
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.workout.viewmodels.WorkoutManagerViewModel

@Composable
fun NutritionScreen(
    navController: NavController,
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Питание",
                navController = navController
            )
        },
        bottomBar = {
            val workoutManagerVm: WorkoutManagerViewModel = hiltViewModel()
            BottomNavigationBar(navController, workoutManagerVm.workoutManager)
        },
        containerColor = Background
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            uiState.error != null -> {
                ErrorState(
                    message = uiState.error ?: "Ошибка загрузки данных",
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    DateSelector(
                        date = selectedDate,
                        onPreviousDay = {
                            viewModel.selectDate(selectedDate.minusDays(1))
                        },
                        onNextDay = {
                            viewModel.selectDate(selectedDate.plusDays(1))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    NutritionSummaryHeader(
                        currentCalories = uiState.currentCalories,
                        goalCalories = uiState.goalCalories,
                        currentMacros = uiState.currentMacros,
                        goalMacros = uiState.goalMacros,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    uiState.meals.forEach { mealSection ->
                        MealSectionCard(
                            title = mealSection.title,
                            items = mealSection.items,
                            onAddClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "food_items_select_mode", true
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "food_items_meal_type", mealSection.type.name
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "food_items_date", selectedDate.toString()
                                )
                                navController.navigate(Screen.FoodItems.route)
                            },
                            onItemClick = { item ->
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "food_items_select_mode", false
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "food_items_meal_type", mealSection.type.name
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "food_items_date", selectedDate.toString()
                                )
                                navController.navigate(Screen.FoodItems.route)
                            },
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
