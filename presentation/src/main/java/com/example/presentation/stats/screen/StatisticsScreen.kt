package com.example.presentation.stats.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.presentation.navigation.Screen
import com.example.presentation.statistics.viewmodels.StatisticsViewModel
import com.example.presentation.ui.components.BottomNavigationBar
import com.example.presentation.ui.components.LoadingIndicator
import com.example.presentation.ui.components.MiniChartPlaceholder
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.DividerColor
import com.example.presentation.ui.theme.Surface
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.workout.viewmodels.WorkoutManagerViewModel

@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Статистика",
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
        if (uiState.isLoading) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // === ТРЕНИРОВКИ ===
                StatsSectionCard(
                    title = "Тренировки",
                    onClick = { navController.navigate(Screen.WorkoutHistory.route) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    StatRowItem(
                        label = "Эта неделя",
                        value = "${uiState.workoutStats.weekCount} тренировки",
                        detail = "тоннаж - ${uiState.workoutStats.weekTonnage} кг",
                        onClick = { navController.navigate(Screen.WorkoutHistory.route) },
                        showChart = false
                    )

                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))

                    StatRowItem(
                        label = "Этот месяц",
                        value = "${uiState.workoutStats.monthCount} тренировок",
                        detail = "тоннаж - ${uiState.workoutStats.monthTonnage} кг",
                        onClick = { navController.navigate(Screen.WorkoutHistory.route) },
                        showChart = false
                    )
                }

                // === ПИТАНИЕ ===
                StatsSectionCard(
                    title = "Питание",
                    onClick = { navController.navigate(Screen.NutritionStats.route) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    val todayMacros = "${uiState.nutritionStats.todayProtein.toInt()}б-${uiState.nutritionStats.todayCarbs.toInt()}у-${uiState.nutritionStats.todayFat.toInt()}ж"
                    StatRowItem(
                        label = "Сегодня",
                        value = "${uiState.nutritionStats.todayCalories} ккал",
                        detail = todayMacros,
                        onClick = { navController.navigate(Screen.NutritionStats.route) },
                        showChart = false
                    )

                    HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))

                    val weekMacros = "${uiState.nutritionStats.weekProtein.toInt()}б-${uiState.nutritionStats.weekCarbs.toInt()}у-${uiState.nutritionStats.weekFat.toInt()}ж"
                    StatRowItem(
                        label = "Эта неделя",
                        value = "${uiState.nutritionStats.weekCalories} ккал",
                        detail = weekMacros,
                        onClick = { navController.navigate(Screen.NutritionStats.route) },
                        showChart = false
                    )
                }

                // === ОСТАЛЬНОЕ ===
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Остальное",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    StatsSectionCard(
                        title = null,
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val typeLabels = mapOf(
                            "weight" to "Вес",
                            "arm" to "Обхват руки",
                            "waist" to "Обхват талии",
                            "chest" to "Обхват груди",
                            "leg" to "Обхват бедра",
                            "shoulder" to "Обхват плеча"
                        )
                        val typeUnits = mapOf(
                            "weight" to "кг",
                            "arm" to "см",
                            "waist" to "см",
                            "chest" to "см",
                            "leg" to "см",
                            "shoulder" to "см"
                        )

                        // Вес всегда показываем, даже если нет записей
                        val weightSummary = uiState.measurements["weight"] ?: StatisticsViewModel.MeasurementSummary()
                        val weightLabel = typeLabels["weight"] ?: "Вес"
                        val weightUnit = typeUnits["weight"] ?: "кг"

                        StatRowItem(
                            label = weightLabel,
                            value = "Сейчас: ${weightSummary.current}${weightUnit}",
                            extraInfo = "Наибольший: ${weightSummary.max}${weightUnit}\nНаименьший: ${weightSummary.min}${weightUnit}",
                            onClick = { navController.navigate(Screen.BodyMeasurements.createRoute("weight")) },
                            showChart = true
                        )

                        // Остальные типы (только если есть записи)
                        uiState.measurements.filter { it.key != "weight" }.entries.forEach { (type, summary) ->
                            HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))

                            val label = typeLabels[type] ?: type
                            val unit = typeUnits[type] ?: ""

                            StatRowItem(
                                label = label,
                                value = "Сейчас: ${summary.current}${unit}",
                                extraInfo = "Наибольший: ${summary.max}${unit}\nНаименьший: ${summary.min}${unit}",
                                onClick = { navController.navigate(Screen.BodyMeasurements.createRoute(type)) },
                                showChart = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatsSectionCard(
    title: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (title != null) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                )
            }
            content()
            if (title == null) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun StatRowItem(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    detail: String? = null,
    extraInfo: String? = null,
    showChart: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            if (detail != null) {
                Text(
                    text = detail,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }

        if (extraInfo != null) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                extraInfo.split("\n").forEach { line ->
                    Text(
                        text = line,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        if (showChart) {
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(50.dp)
            ) {
                MiniChartPlaceholder(title = "Мини-график")
            }
        }
    }
}
