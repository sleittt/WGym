package com.example.presentation.meal.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.presentation.ui.components.MealSectionCard
import com.example.presentation.ui.components.NutritionSummaryHeader
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.workout.viewmodels.WorkoutManagerViewModel
import java.time.LocalDate

@Composable
fun NutritionScreen(
    navController: NavController,
    dateString: String? = null,
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    // Устанавливаем дату из параметра ТОЛЬКО при изменении dateString
    LaunchedEffect(dateString) {
        if (!dateString.isNullOrBlank()) {
            try {
                val parsedDate = LocalDate.parse(dateString)
                if (parsedDate != selectedDate) {
                    viewModel.selectDate(parsedDate)
                }
            } catch (e: Exception) {
                // ignore invalid date
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Питание",
                navController = navController,
                containerColor = Background
            )
        },
        bottomBar = {
            val workoutManagerVm: WorkoutManagerViewModel = hiltViewModel()
            BottomNavigationBar(navController, workoutManagerVm.workoutManager)
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(Background),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Селектор даты
            DateSelector(
                date = selectedDate,
                onPreviousDay = {
                    viewModel.selectDate(selectedDate.minusDays(1))
                },
                onNextDay = {
                    viewModel.selectDate(selectedDate.plusDays(1))
                }
            )

            // Заголовок с калориями и целью
            NutritionSummaryHeader(
                currentCalories = uiState.currentCalories,
                goalCalories = uiState.goalCalories,
                currentMacros = uiState.currentMacros,
                goalMacros = uiState.goalMacros
            )

            // Секции приёмов пищи
            uiState.meals.forEach { mealSection ->
                MealSectionCard(
                    title = mealSection.title,
                    items = mealSection.items,
                    onAddClick = {
                        // Передаём текущую выбранную дату в FoodItemsScreen
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "food_items_date", selectedDate.toString()
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "food_items_meal_type", mealSection.type.name
                        )
                        navController.navigate(Screen.FoodItems.route)
                    },
                    onItemClick = { item ->
                        // TODO: navigate to meal item detail
                    }
                )
            }
        }
    }
}
