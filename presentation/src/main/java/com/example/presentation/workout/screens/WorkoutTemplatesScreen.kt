package com.example.presentation.workout.screens

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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
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
import com.example.domain.model.workout.WorkoutTemplate
import com.example.presentation.navigation.Screen
import com.example.presentation.ui.components.BottomNavigationBar
import com.example.presentation.ui.components.Card
import com.example.presentation.ui.components.DropdownAction
import com.example.presentation.ui.components.DropdownMenu
import com.example.presentation.ui.components.LoadingIndicator
import com.example.presentation.ui.components.PlayIconButton
import com.example.presentation.ui.components.SectionTitle
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.workout.viewmodels.WorkoutManagerViewModel
import com.example.presentation.workout.viewmodels.WorkoutTemplatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutTemplatesScreen(
    navController: NavController,
    viewModel: WorkoutTemplatesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPinned by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Шаблоны тренировок",
                navController = navController,
                containerColor = Background
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.WorkoutTemplateDetail.createRoute("0"))
                },
                containerColor = PrimaryRed,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        },
        bottomBar = {
            val workoutManagerVm: WorkoutManagerViewModel = hiltViewModel()
            BottomNavigationBar(navController, workoutManagerVm.workoutManager)
        },
        containerColor = Background
    ) { padding ->
        if (uiState.isLoading && uiState.templates.isEmpty()) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                // Закреплённые шаблоны
                if (uiState.pinnedTemplates.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Закрепленные шаблоны",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            IconButton(onClick = { showPinned = !showPinned }) {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    if (showPinned) {
                        items(uiState.pinnedTemplates) { template ->
                            PinnedTemplateCard(
                                template = template,
                                onPlay = {
                                    navController.navigate(Screen.ActiveWorkout.createRoute(template.id.toString()))
                                }
                            )
                        }
                    }
                }

                // Все шаблоны
                item {
                    SectionTitle(
                        title = "Все шаблоны",
                        actionText = "Создать",
                        onActionClick = {
                            navController.navigate(Screen.WorkoutTemplateDetail.createRoute("0"))
                        }
                    )
                }

                items(uiState.templates) { template ->
                    WorkoutTemplateListCard(
                        template = template,
                        onPlay = {
                            navController.navigate(Screen.ActiveWorkout.createRoute(template.id.toString()))
                        },
                        onEdit = {
                            navController.navigate(Screen.WorkoutTemplateDetail.createRoute(template.id.toString()))
                        },
                        onDelete = { viewModel.deleteTemplate(template.id.toString()) },
                        onDuplicate = { viewModel.duplicateTemplate(template.id.toString()) }
                    )
                }
            }
        }
    }
}

@Composable
fun PinnedTemplateCard(
    template: WorkoutTemplate,
    onPlay: () -> Unit
) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    template.name,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Закреплено",
                    tint = PrimaryRed,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Упражнения в 2 колонки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    template.exercise.take(3).forEach { ex ->
                        Text(
                            "${ex.sets.size} x ${ex.template.name}",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    template.exercise.drop(3).take(3).forEach { ex ->
                        Text(
                            "${ex.sets.size} x ${ex.template.name}",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                PlayIconButton(onClick = onPlay, size = 44.dp)
            }
        }
    }
}

@Composable
fun WorkoutTemplateListCard(
    template: WorkoutTemplate,
    onPlay: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit
) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    template.name,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                DropdownMenu(
                    actions = listOf(
                        DropdownAction("Редактировать", onClick = onEdit),
                        DropdownAction("Дублировать", onClick = onDuplicate),
                        DropdownAction("Удалить", isDanger = true, onClick = onDelete)
                    ),
                    iconSize = 20.dp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    template.exercise.take(3).forEach { ex ->
                        Text(
                            "${ex.sets.size} x ${ex.template.name}",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    template.exercise.drop(3).take(3).forEach { ex ->
                        Text(
                            "${ex.sets.size} x ${ex.template.name}",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                PlayIconButton(onClick = onPlay, size = 44.dp)
            }
        }
    }
}