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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.domain.model.workout.SetType
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

// ─── Цвета типов подходов ───
private val WarmupColor = PrimaryGreen
private val FailureColor = PrimaryRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    navController: NavController,
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val selectedId by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("selected_exercise_template_id", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(selectedId) {
        selectedId?.let {
            viewModel.fetchAndAddExercise(it)
            navController.currentBackStackEntry?.savedStateHandle?.set("selected_exercise_template_id", null)
        }
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

    if (uiState.showFinishDialog) {
        FinishWorkoutDialog(
            onMarkAllCompleted = { viewModel.markAllSetsCompletedAndFinish() },
            onRemoveUncompleted = { viewModel.removeUncompletedSetsAndFinish() },
            onDismiss = { viewModel.dismissFinishDialog() }
        )
    }

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
                        enabled = !uiState.isFinished
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Завершить",
                            tint = if (!uiState.isFinished) PrimaryGreen else TextSecondary,
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
                    sets = exerciseWithSets.sets,
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
                    },
                    onChangeSetType = { setIndex, type ->
                        viewModel.changeSetType(index, setIndex, type)
                    },
                    onAddSet = { viewModel.addSet(index) },
                    onRemoveSet = { setIndex -> viewModel.removeSet(index, setIndex) },
                    onStartRestTimer = { setIndex -> viewModel.startRestTimer(index, setIndex) }
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
fun FinishWorkoutDialog(
    onMarkAllCompleted: () -> Unit,
    onRemoveUncompleted: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Завершить тренировку?",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                "Не все подходы выполнены. Что сделать с неотмеченными подходами?",
                color = TextSecondary,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onMarkAllCompleted) {
                Text(
                    "Отметить все",
                    color = PrimaryGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            Column {
                TextButton(onClick = onRemoveUncompleted) {
                    Text(
                        "Удалить неотмеченные",
                        color = PrimaryRed,
                        fontSize = 14.sp
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text(
                        "Отмена",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        },
        containerColor = Surface
    )
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

@Composable
fun ExerciseCardWithSets(
    exerciseName: String,
    sets: List<WorkoutManager.SetData>,
    onSetClick: (Int) -> Unit,
    onWeightChange: (Int, String) -> Unit,
    onRepsChange: (Int, String) -> Unit,
    onSkipRest: (Int) -> Unit,
    onAdjustRestTime: (Int, Int) -> Unit,
    onChangeSetType: (Int, SetType) -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: (Int) -> Unit,
    onStartRestTimer: (Int) -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    exerciseName,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Тип", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(40.dp))
                Text("Пред.", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Вес", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Повт.", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(50.dp))
                Text("", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(24.dp))
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
                    onChangeSetType = { onChangeSetType(index, it) },
                    onRemoveSet = { onRemoveSet(index) },
                    onStartRestTimer = { onStartRestTimer(index) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onAddSet) {
                    Text("+ Подход", color = PrimaryRed, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun SetRow(
    setData: WorkoutManager.SetData,
    setIndex: Int,
    onClick: () -> Unit,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onSkipRest: () -> Unit,
    onAdjustRestTime: (Int) -> Unit,
    onChangeSetType: (SetType) -> Unit,
    onRemoveSet: () -> Unit,
    onStartRestTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        setData.isCompleted -> PrimaryGreen.copy(alpha = 0.2f)
        setData.type == SetType.WARMUP -> SurfaceVariant.copy(alpha = 0.7f)
        else -> SurfaceVariant.copy(alpha = 0.5f)
    }

    var showTypeMenu by remember { mutableStateOf(false) }

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
                // Тип подхода (кликабельный)
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (setData.type) {
                                SetType.WARMUP -> WarmupColor.copy(alpha = 0.2f)
                                SetType.FAILURE -> FailureColor.copy(alpha = 0.2f)
                                else -> Color.Transparent
                            }
                        )
                        .clickable { showTypeMenu = true }
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        setData.setNumber,
                        color = when (setData.type) {
                            SetType.WARMUP -> WarmupColor
                            SetType.FAILURE -> FailureColor
                            else -> if (setData.isCompleted) PrimaryGreen else TextPrimary
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

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

                // Удалить подход
                IconButton(
                    onClick = onRemoveSet,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Удалить подход",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Меню выбора типа подхода
        if (showTypeMenu) {
            AlertDialog(
                onDismissRequest = { showTypeMenu = false },
                title = { Text("Тип подхода", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        SetType.entries.forEach { type ->
                            TextButton(
                                onClick = {
                                    onChangeSetType(type)
                                    showTypeMenu = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    type.displayName,
                                    color = when (type) {
                                        SetType.WARMUP -> WarmupColor
                                        SetType.FAILURE -> FailureColor
                                        else -> TextPrimary
                                    },
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showTypeMenu = false }) {
                        Text("Отмена", color = TextSecondary)
                    }
                },
                containerColor = Surface
            )
        }

        // Таймер отдыха
        RestTimerBar(
            progress = setData.restProgress,
            isCompleted = setData.isCompleted,
            onSkip = onSkipRest,
            onAdjustTime = onAdjustRestTime,
            onStartTimer = onStartRestTimer,
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
    onStartTimer: () -> Unit,
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

            // Клик по таймеру запускает отдых вручную
            Box(
                modifier = Modifier.clickable { onStartTimer() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isRunning) "Отдых: ${progress.formattedTime}"
                    else "Отдых: ${progress.formattedConfiguredTime} (нажми)",
                    color = if (isRunning) PrimaryGreen else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (isRunning) FontWeight.Medium else FontWeight.Normal
                )
            }

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
