package com.example.presentation.stats.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.presentation.navigation.Screen
import com.example.presentation.stats.viewmodels.NutritionStatsViewModel
import com.example.presentation.ui.components.BarChart
import com.example.presentation.ui.components.BottomNavigationBar
import com.example.presentation.ui.components.Chip
import com.example.presentation.ui.components.LoadingIndicator
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryGreen
import com.example.presentation.ui.theme.Surface
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.workout.viewmodels.WorkoutManagerViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NutritionStatsScreen(
    navController: NavController,
    viewModel: NutritionStatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Статистика питания",
                navController = navController
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
        ) {
            // Переключатель неделя/месяц
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Chip(
                    text = "Неделя",
                    isSelected = uiState.mode == NutritionStatsViewModel.PeriodMode.WEEK,
                    onClick = { viewModel.setMode(NutritionStatsViewModel.PeriodMode.WEEK) }
                )
                Chip(
                    text = "Месяц",
                    isSelected = uiState.mode == NutritionStatsViewModel.PeriodMode.MONTH,
                    onClick = { viewModel.setMode(NutritionStatsViewModel.PeriodMode.MONTH) }
                )
            }

            if (uiState.isLoading) {
                LoadingIndicator(modifier = Modifier.height(200.dp))
            } else {
                // График — barData теперь в uiState
                if (uiState.barData.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                            .background(Surface, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (uiState.mode == NutritionStatsViewModel.PeriodMode.WEEK)
                                "Калории по дням" else "Калории по неделям",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        BarChart(
                            bars = uiState.barData,
                            onBarClick = { index ->
                                val date = viewModel.getSelectedDate(index)
                                if (date != null) {
                                    navController.navigate(
                                        Screen.Nutrition.createRoute(date.toString())
                                    )
                                }
                            },
                            barColor = PrimaryGreen,
                            chartHeight = 200.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Детализация по приёмам пищи
                Text(
                    text = "Детализация",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                )

                if (uiState.mode == NutritionStatsViewModel.PeriodMode.WEEK && uiState.dailyData.isNotEmpty()) {
                    uiState.dailyData.forEach { day ->
                        DaySummaryCard(
                            date = day.date,
                            calories = day.calories,
                            protein = day.protein,
                            fat = day.fat,
                            carbs = day.carbs,
                            onClick = {
                                navController.navigate(
                                    Screen.Nutrition.createRoute(day.date.toString())
                                )
                            }
                        )
                    }
                } else if (uiState.mode == NutritionStatsViewModel.PeriodMode.MONTH && uiState.weeklyData.isNotEmpty()) {
                    uiState.weeklyData.forEach { week ->
                        WeekSummaryCard(
                            weekNumber = week.weekNumber,
                            startDate = week.startDate,
                            endDate = week.endDate,
                            calories = week.calories,
                            protein = week.protein,
                            fat = week.fat,
                            carbs = week.carbs,
                            onClick = {
                                navController.navigate(
                                    Screen.Nutrition.createRoute(week.startDate.toString())
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DaySummaryCard(
    date: LocalDate,
    calories: Int,
    protein: Double,
    fat: Double,
    carbs: Double,
    onClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd MMMM", Locale("ru"))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
            .background(Surface, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date.format(formatter),
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$calories ккал",
                color = PrimaryGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${protein.toInt()}г белков · ${fat.toInt()}г жиров · ${carbs.toInt()}г углеводов",
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun WeekSummaryCard(
    weekNumber: Int,
    startDate: LocalDate,
    endDate: LocalDate,
    calories: Int,
    protein: Double,
    fat: Double,
    carbs: Double,
    onClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM", Locale("ru"))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
            .background(Surface, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Неделя $weekNumber (${startDate.format(formatter)} - ${endDate.format(formatter)})",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$calories ккал",
                color = PrimaryGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${protein.toInt()}г белков · ${fat.toInt()}г жиров · ${carbs.toInt()}г углеводов",
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}
