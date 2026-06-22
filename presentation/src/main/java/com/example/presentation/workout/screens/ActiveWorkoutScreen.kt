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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import com.example.domain.manager.WorkoutManager
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
    val listState = rememberLazyListState()

    LaunchedEffect(templateId) {
        viewModel.loadTemplate(templateId)
    }

    val activeTimer = uiState.activeRestTimer
    val isActiveTimerVisible by remember(activeTimer, listState) {
        derivedStateOf {
            if (activeTimer == null) return@derivedStateOf true
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            val exerciseItemIndex = if (uiState.templateName.isNotBlank()) {
                activeTimer.exerciseIndex + 1
            } else {
                activeTimer.exerciseIndex
            }
            visibleItems.any { it.index == exerciseItemIndex }
        }
    }

    val showFloatingTimer = activeTimer != null && activeTimer.isRunning && !isActiveTimerVisible

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            uiState.elapsedTimeFormatted,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (showFloatingTimer) {
                            Spacer(modifier = Modifier.width(12.dp))
                            FloatingRestTimer(
                                timer = activeTimer!!,
                                onSkip = {
                                    viewModel.skipRestTimer(
                                        activeTimer.exerciseIndex,
                                        activeTimer.setIndex
                                    )
                                }
                            )
                        }
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
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
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
                            restProgress = set.restProgress,
                            restDurationSeconds = set.restDuration.inWholeSeconds.toInt()
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
                    },
                    onSkipRest = { setIndex ->
                        viewModel.skipRestTimer(index, setIndex)
                    },
                    onAdjustRestTime = { setIndex, delta ->
                        viewModel.adjustRestTime(index, setIndex, delta)
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

@Composable
fun FloatingRestTimer(
    timer: WorkoutManager.ActiveRestTimer,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(PrimaryGreen.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            timer.formattedTime,
            color = PrimaryGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        LinearProgressIndicator(
            progress = { timer.progress.progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .width(50.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = PrimaryGreen,
            trackColor = PrimaryGreen.copy(alpha = 0.2f)
        )
    }
}

data class SetData(
    val setNumber: String,
    val previous: String,
    val weight: String,
    val reps: String,
    val isCompleted: Boolean = false,
    val restProgress: WorkoutManager.RestProgress = WorkoutManager.RestProgress(),
    val restDurationSeconds: Int = 90
)

@Composable
fun ExerciseCardWithSets(
    exerciseName: String,
    sets: List<SetData>,
    onSetClick: (Int) -> Unit,
    onWeightChange: (Int, String) -> Unit,
    onRepsChange: (Int, String) -> Unit,
    onSkipRest: (Int) -> Unit,
    onAdjustRestTime: (Int, Int) -> Unit
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
                    onSkipRest = { onSkipRest(index) },
                    onAdjustRestTime = { delta -> onAdjustRestTime(index, delta) },
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
    onSkipRest: () -> Unit,
    onAdjustRestTime: (Int) -> Unit,
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

        RestTimerBar(
            progress = setData.restProgress,
            isCompleted = setData.isCompleted,
            onSkip = onSkipRest,
            onAdjustTime = onAdjustRestTime,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun RestTimerBar(
    progress: WorkoutManager.RestProgress,
    isCompleted: Boolean,
    onSkip: () -> Unit,
    onAdjustTime: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isRunning = progress.isRunning
    val hasTimeLeft = progress.remainingSeconds > 0

    val barColor = when {
        isRunning && hasTimeLeft -> PrimaryGreen
        isCompleted && !hasTimeLeft -> PrimaryGreen.copy(alpha = 0.5f)
        else -> TextSecondary.copy(alpha = 0.3f)
    }

    val bgColor = when {
        isRunning -> PrimaryGreen.copy(alpha = 0.1f)
        isCompleted -> PrimaryGreen.copy(alpha = 0.05f)
        else -> SurfaceVariant.copy(alpha = 0.3f)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RestTimeButton(text = "−", onClick = { onAdjustTime(-15) })
                Spacer(modifier = Modifier.width(4.dp))
                RestTimeButton(text = "−−", onClick = { onAdjustTime(-30) })
            }

            Text(
                if (isRunning) "Отдых: ${progress.formattedTime}"
                else "Отдых: ${progress.formattedConfiguredTime}",
                color = if (isRunning) PrimaryGreen else TextSecondary,
                fontSize = 13.sp,
                fontWeight = if (isRunning) FontWeight.Medium else FontWeight.Normal
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                RestTimeButton(text = "+", onClick = { onAdjustTime(15) })
                Spacer(modifier = Modifier.width(4.dp))
                RestTimeButton(text = "++", onClick = { onAdjustTime(30) })
                Spacer(modifier = Modifier.width(8.dp))

                if (isRunning && hasTimeLeft) {
                    IconButton(onClick = onSkip, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Пропустить отдых",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        val progressValue = when {
            isRunning -> progress.progress.coerceIn(0f, 1f)
            isCompleted -> 0f
            else -> 1f
        }

        LinearProgressIndicator(
            progress = { progressValue },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = barColor.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun RestTimeButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}