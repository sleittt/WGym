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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.presentation.ui.components.Button
import com.example.presentation.ui.components.Card
import com.example.presentation.ui.components.DropdownAction
import com.example.presentation.ui.components.DropdownMenu
import com.example.presentation.ui.components.LoadingIndicator
import com.example.presentation.ui.components.TextField
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.Surface
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.workout.viewmodels.WorkoutTemplateDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutTemplateDetailScreen(
    navController: NavController,
    templateId: String = "0"
) {
    val viewModel: WorkoutTemplateDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Получаем выбранное упражнение из ExerciseTemplatesScreen (режим выбора)
    val selectedExerciseId by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("selected_exercise_template_id", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(selectedExerciseId) {
        selectedExerciseId?.let { id ->
            viewModel.fetchAndAddExercise(id)
            navController.currentBackStackEntry?.savedStateHandle?.set("selected_exercise_template_id", null)
        }
    }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) {
            navController.navigateUp()
            viewModel.consumeSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isNew) "Новый шаблон" else "Имя шаблона",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
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
                    if (!uiState.isNew) {
                        // Кнопка закрепления
                        IconButton(onClick = { viewModel.togglePin() }) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = if (uiState.isPinned) "Открепить" else "Закрепить",
                                tint = if (uiState.isPinned) PrimaryRed else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        DropdownMenu(
                            actions = listOf(
                                DropdownAction("Дублировать", onClick = { viewModel.duplicate() }),
                                DropdownAction("Удалить", isDanger = true, onClick = {
                                    viewModel.delete()
                                    navController.navigateUp()
                                })
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.ExerciseTemplates.createRoute(selectMode = true))
                },
                containerColor = PrimaryRed,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить упражнение")
            }
        },
        containerColor = Background
    ) { padding ->
        if (uiState.isLoading) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(padding)
            ) {
                // Название шаблона - редактируемое поле
                item {
                    Text(
                        "Название шаблона",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    TextField(
                        value = uiState.name,
                        onValueChange = { viewModel.updateName(it) }
                    )
                }

                // Упражнения
                if (uiState.exercises.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Surface)
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Нет упражнений. Нажмите + чтобы добавить.",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(uiState.exercises.size) { index ->
                        ExerciseEditorCard(
                            exercise = uiState.exercises[index],
                            onRemoveExercise = { viewModel.removeExercise(index) },
                            onAddSet = { viewModel.addSet(index) },
                            onRemoveSet = { setIdx -> viewModel.removeSet(index, setIdx) },
                            onUpdateSet = { setIdx, load, reps ->
                                viewModel.updateSet(index, setIdx, load, reps)
                            }
                        )
                    }
                }

                // Сохранить
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        text = if (uiState.isSaving) "Сохранение..." else "Сохранить шаблон",
                        onClick = { viewModel.save() },
                        isPrimary = true,
                        enabled = !uiState.isSaving
                    )
                }

                // Ошибка
                uiState.error?.let { error ->
                    item {
                        Text(
                            error,
                            color = PrimaryRed,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseEditorCard(
    exercise: com.example.domain.model.workout.Exercise,
    onRemoveExercise: () -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: (Int) -> Unit,
    onUpdateSet: (Int, Float, Int) -> Unit
) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    exercise.template.name,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemoveExercise, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Удалить",
                        tint = PrimaryRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Заголовок таблицы подходов
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Подход", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(40.dp))
                Text("Пред.", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Вес", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Повт.", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(50.dp))
                Spacer(modifier = Modifier.width(24.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Подходы
            exercise.sets.forEachIndexed { setIndex, set ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        when (set.type) {
                            com.example.domain.model.workout.SetType.WARMUP -> "W"
                            com.example.domain.model.workout.SetType.FAILURE -> "F"
                            else -> (setIndex + 1).toString()
                        },
                        color = TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.width(40.dp)
                    )

                    Text(
                        "${set.load.toInt()} кг x ${set.reps}",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )

                    var weightText by remember(set.load) { mutableStateOf(set.load.toString()) }
                    Box(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                        TextField(
                            value = weightText,
                            onValueChange = {
                                weightText = it
                                it.toFloatOrNull()?.let { w -> onUpdateSet(setIndex, w, set.reps) }
                            },
                            singleLine = true
                        )
                    }

                    var repsText by remember(set.reps) { mutableStateOf(set.reps.toString()) }
                    Box(modifier = Modifier.width(50.dp)) {
                        TextField(
                            value = repsText,
                            onValueChange = {
                                repsText = it
                                it.toIntOrNull()?.let { r -> onUpdateSet(setIndex, set.load, r) }
                            },
                            singleLine = true
                        )
                    }

                    IconButton(
                        onClick = { onRemoveSet(setIndex) },
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

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onAddSet) {
                Text("+ Подход", color = PrimaryRed, fontSize = 14.sp)
            }
        }
    }
}