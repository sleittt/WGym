package com.example.presentation.stats.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.domain.model.workout.Workout
import com.example.presentation.stats.viewmodels.WorkoutTemplateHistoryViewModel
import com.example.presentation.ui.components.BottomNavigationBar
import com.example.presentation.ui.components.LoadingIndicator
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryGreen
import com.example.presentation.ui.theme.Surface
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.workout.viewmodels.WorkoutManagerViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WorkoutTemplateHistoryScreen(
    navController: NavController,
    templateId: String,
    viewModel: WorkoutTemplateHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Загружаем данные при входе
    viewModel.loadTemplateHistory(templateId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = uiState.templateName,
                navController = navController
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
        } else if (uiState.workouts.isEmpty()) {
            EmptyTemplateHistoryState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Статистика шаблона
                item {
                    TemplateStatsCard(
                        totalWorkouts = uiState.workouts.size,
                        totalTonnage = uiState.totalTonnage,
                        avgTonnage = uiState.avgTonnage
                    )
                }

                // Список тренировок
                items(uiState.workouts) { workout ->
                    TemplateWorkoutCard(
                        workout = workout,
                        onClick = {
                            // TODO: navigate to workout detail
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplateStatsCard(
    totalWorkouts: Int,
    totalTonnage: Int,
    avgTonnage: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Статистика шаблона",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Тренировок", totalWorkouts.toString())
            StatItem("Общий тоннаж", "$totalTonnage кг")
            StatItem("Средний тоннаж", "$avgTonnage кг")
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = PrimaryGreen,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun TemplateWorkoutCard(
    workout: Workout,
    onClick: () -> Unit
) {
    val tonnage = workout.exercises.sumOf { ex ->
        ex.sets.sumOf { set -> (set.load * set.reps).toDouble() }
    }.toInt()

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ru"))

    Column(
        modifier = Modifier
            .fillMaxWidth()
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
                text = workout.date.format(dateFormatter),
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$tonnage кг",
                color = PrimaryGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = workout.exercises.joinToString(", ") { it.template.name },
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun EmptyTemplateHistoryState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Нет тренировок по этому шаблону",
            color = TextSecondary,
            fontSize = 16.sp
        )
    }
}
