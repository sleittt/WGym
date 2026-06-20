package com.example.presentation.workout.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.presentation.navigation.Screen
import com.example.presentation.ui.components.EmptyListState
import com.example.presentation.ui.components.TextField
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryGreen
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.Surface
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.workout.viewmodels.ActiveWorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    navController: NavController,
    templateId: String = "",
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(templateId) {
        viewModel.loadTemplate(templateId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            uiState.elapsedTimeFormatted,
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.finishWorkout() },
                        enabled = uiState.canFinish && !uiState.isFinished
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Завершить",
                            tint = if (uiState.canFinish && !uiState.isFinished) PrimaryGreen else TextSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.ExerciseTemplates.createRoute(selectMode = true)) },
                containerColor = PrimaryRed,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить упражнение")
            }
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
            // Название шаблона
            if (uiState.templateName.isNotBlank()) {
                item {
                    Text(
                        uiState.templateName,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(uiState.exercises.size) { index ->
                val exerciseWithSets = uiState.exercises[index]
                ExerciseCardWithSets(
                    exerciseName = exerciseWithSets.exercise.template.name,
                    sets = exerciseWithSets.sets.map { set ->
                        SetData(
                            setNumber = set.setNumber,
                            previous = set.previous,
                            weight = set.weight,
                            reps = set.reps,
                            isCompleted = set.isCompleted,
                            restTime = set.restTime?.inWholeSeconds?.let { sec ->
                                String.format("%02d:%02d", sec / 60, sec % 60)
                            } ?: ""
                        )
                    },
                    onSetClick = { setIndex ->
                        viewModel.toggleSetCompletion(index, setIndex)
                    },
                    onWeightChange = { setIndex, weight ->
                        viewModel.updateSetWeight(index, setIndex, weight)
                    },
                    onRepsChange = { setIndex, reps ->
                        viewModel.updateSetReps(index, setIndex, reps)
                    }
                )
            }

            if (uiState.exercises.isEmpty()) {
                item {
                    EmptyListState(
                        text = "Добавить упражнение",
                        onClick = { navController.navigate(Screen.ExerciseTemplates.createRoute(selectMode = true)) }
                    )
                }
            }

            if (uiState.isFinished) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryGreen.copy(alpha = 0.2f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Тренировка завершена!",
                            color = PrimaryGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

data class SetData(
    val setNumber: String,
    val previous: String,
    val weight: String,
    val reps: String,
    val isCompleted: Boolean = false,
    val restTime: String = ""
)

@Composable
fun ExerciseCardWithSets(
    exerciseName: String,
    sets: List<SetData>,
    onSetClick: (Int) -> Unit,
    onWeightChange: (Int, String) -> Unit,
    onRepsChange: (Int, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                exerciseName,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Подход", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(40.dp))
                Text("Пред.", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Вес", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Повт.", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(50.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            sets.forEachIndexed { index, set ->
                SetRow(
                    setData = set,
                    setIndex = index,
                    onClick = { onSetClick(index) },
                    onWeightChange = { onWeightChange(index, it) },
                    onRepsChange = { onRepsChange(index, it) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun SetRow(
    setData: SetData,
    setIndex: Int,
    onClick: () -> Unit,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        setData.isCompleted -> PrimaryGreen.copy(alpha = 0.2f)
        setData.setNumber == "W" -> SurfaceVariant
        else -> SurfaceVariant.copy(alpha = 0.5f)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    setData.setNumber,
                    color = if (setData.isCompleted) PrimaryGreen else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(40.dp)
                )

                Text(
                    setData.previous,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )

                var weightText by remember(setData.weight) { mutableStateOf(setData.weight) }
                Box(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                    TextField(
                        value = weightText,
                        onValueChange = {
                            weightText = it
                            onWeightChange(it)
                        },
                        singleLine = true,
                        modifier = Modifier.height(40.dp)
                    )
                }

                var repsText by remember(setData.reps) { mutableStateOf(setData.reps) }
                Box(modifier = Modifier.width(50.dp)) {
                    TextField(
                        value = repsText,
                        onValueChange = {
                            repsText = it
                            onRepsChange(it)
                        },
                        singleLine = true,
                        modifier = Modifier.height(40.dp)
                    )
                }
            }
        }

        if (setData.restTime.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(PrimaryGreen)
            )
            Text(
                setData.restTime,
                color = PrimaryGreen,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 50.dp, top = 2.dp)
            )
        }
    }
}