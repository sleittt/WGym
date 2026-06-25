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
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.domain.model.workout.MuscleGroup
import com.example.domain.model.workout.Workout
import com.example.presentation.navigation.Screen
import com.example.presentation.stats.viewmodels.WorkoutHistoryViewModel
import com.example.presentation.ui.components.BottomNavigationBar
import com.example.presentation.ui.components.Chip
import com.example.presentation.ui.components.LoadingIndicator
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryGreen
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.Surface
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.workout.viewmodels.WorkoutManagerViewModel

@Composable
fun WorkoutHistoryScreen(
    navController: NavController,
    viewModel: WorkoutHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "История тренировок",
                navController = navController
            )
        },
        bottomBar = {
            val workoutManagerVm: WorkoutManagerViewModel = hiltViewModel()
            BottomNavigationBar(navController, workoutManagerVm.workoutManager)
        },
        containerColor = Background
    ) { padding ->
        if (uiState.isLoading && uiState.allWorkouts.isEmpty()) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Фильтр по группам мышц
                item {
                    MuscleGroupFilter(
                        groups = uiState.availableMuscleGroups,
                        selected = uiState.selectedMuscleGroup,
                        onSelect = { viewModel.setMuscleGroupFilter(it) }
                    )
                }

                // Список тренировок по датам
                if (uiState.groupedWorkouts.isEmpty()) {
                    item {
                        EmptyHistoryState()
                    }
                } else {
                    uiState.groupedWorkouts.forEach { group ->
                        item {
                            WorkoutDateGroup(
                                group = group,
                                onWorkoutClick = { workout ->
                                    // TODO: navigate to workout detail
                                },
                                onTemplateHistoryClick = { workout ->
                                    navController.navigate(
                                        Screen.WorkoutTemplateHistory.createRoute(workout.template.id.toString())
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MuscleGroupFilter(
    groups: List<MuscleGroup>,
    selected: MuscleGroup?,
    onSelect: (MuscleGroup?) -> Unit
) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
        Text(
            text = "Фильтр по группам мышц",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Chip(
                    text = "Все",
                    isSelected = selected == null,
                    onClick = { onSelect(null) }
                )
            }
            items(groups) { group ->
                Chip(
                    text = group.displayName,
                    isSelected = selected == group,
                    onClick = { onSelect(group) }
                )
            }
        }
    }
}

@Composable
private fun WorkoutDateGroup(
    group: WorkoutHistoryViewModel.WorkoutGroup,
    onWorkoutClick: (Workout) -> Unit,
    onTemplateHistoryClick: (Workout) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Дата
        Text(
            text = group.dateLabel,
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
        )

        // Тренировки этой даты
        group.workouts.forEachIndexed { index, workout ->
            WorkoutHistoryCard(
                workout = workout,
                onClick = { onWorkoutClick(workout) },
                onTemplateClick = { onTemplateHistoryClick(workout) }
            )
            if (index < group.workouts.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun WorkoutHistoryCard(
    workout: Workout,
    onClick: () -> Unit,
    onTemplateClick: () -> Unit
) {
    val tonnage = workout.exercises.sumOf { ex ->
        ex.sets.sumOf { set -> (set.load * set.reps).toDouble() }
    }.toInt()

    val muscleGroups = workout.exercises
        .flatMap { it.template.muscleGroups }
        .distinct()
        .joinToString(", ") { it.displayName }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        // Название шаблона + тоннаж
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = workout.template.name,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${tonnage} кг",
                color = PrimaryGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Группы мышц
        if (muscleGroups.isNotEmpty()) {
            Text(
                text = muscleGroups,
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Упражнения
        Text(
            text = workout.exercises.joinToString(", ") { it.template.name },
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Кнопка истории шаблона
        Text(
            text = "История шаблона →",
            color = PrimaryRed,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable { onTemplateClick() }
        )
    }
}

@Composable
private fun EmptyHistoryState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Нет тренировок",
            color = TextSecondary,
            fontSize = 16.sp
        )
        Text(
            text = "Начните тренировку, чтобы она появилась здесь",
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
