package com.example.presentation.workout.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.domain.model.workout.ExerciseTemplate
import com.example.domain.model.workout.MuscleGroup
import com.example.presentation.navigation.Screen
import com.example.presentation.ui.components.Button
import com.example.presentation.ui.components.FilterChip
import com.example.presentation.ui.components.LoadingIndicator
import com.example.presentation.ui.components.TextField
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.workout.viewmodels.ExerciseTemplatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseTemplatesScreen(
    navController: NavController,
    selectMode: Boolean = false,
    viewModel: ExerciseTemplatesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = if (selectMode) "Выберите упражнение" else "Упражнения",
                navController = navController,
                containerColor = Background
            )
        },
        floatingActionButton = {
            if (!selectMode) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.ExerciseCreate.route) },
                    containerColor = PrimaryRed,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                }
            }
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Поисковая строка сверху
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Поиск",
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Фильтры групп мышц
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    text = "Все",
                    isSelected = uiState.filterMuscleGroup == null,
                    onClick = { viewModel.setFilter(null) }
                )
                MuscleGroup.entries.take(4).forEach { group ->
                    FilterChip(
                        text = group.displayName,
                        isSelected = uiState.filterMuscleGroup == group,
                        onClick = { viewModel.setFilter(group) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Контент с весом 1f чтобы занять всё доступное пространство
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.isLoading && uiState.filteredTemplates.isEmpty()) {
                    LoadingIndicator()
                } else {
                    val filtered = uiState.filteredTemplates.filter {
                        it.name.contains(searchQuery, ignoreCase = true)
                    }

                    if (filtered.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Нет упражнений",
                                color = TextSecondary,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filtered) { template ->
                                ExerciseTemplateListRow(
                                    template = template,
                                    selectMode = selectMode,
                                    onClick = {
                                        // Клик по телу ВСЕГДА открывает детальное окно
                                        navController.navigate(
                                            Screen.ExerciseDetail.createRoute(template.id.toString())
                                        )
                                    },
                                    onAddToWorkout = {
                                        // Клик по + только в selectMode добавляет в тренировку
                                        navController.previousBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("selected_exercise_template_id", template.id.toString())
                                        navController.navigateUp()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (selectMode) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    text = "Добавить упражнение",
                    onClick = {
                        navController.navigate(Screen.ExerciseCreate.route)
                    },
                    isPrimary = false
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ExerciseTemplateListRow(
    template: ExerciseTemplate,
    selectMode: Boolean,
    onClick: () -> Unit,
    onAddToWorkout: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                template.name,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            if (template.muscleGroups.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    template.muscleGroups.joinToString(", ") { it.displayName },
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        if (selectMode) {
            IconButton(onClick = onAddToWorkout) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Добавить в тренировку",
                    tint = PrimaryRed,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}