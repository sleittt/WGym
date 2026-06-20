package com.example.presentation.workout.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.domain.model.workout.MuscleGroup
import com.example.presentation.ui.components.Button
import com.example.presentation.ui.components.Card
import com.example.presentation.ui.components.Chip
import com.example.presentation.ui.components.DangerButton
import com.example.presentation.ui.components.TextField
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.workout.viewmodels.ExerciseTemplatesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExerciseDetailScreen(
    navController: NavController,
    exerciseId: String = "",
    viewModel: ExerciseTemplatesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val exercise = remember(exerciseId, uiState.templates) {
        uiState.templates.find { it.id.toString() == exerciseId }
    }

    var name by remember(exercise) { mutableStateOf(exercise?.name ?: "") }
    var selectedMuscleGroups by remember(exercise) { mutableStateOf(exercise?.muscleGroups ?: emptyList()) }
    var description by remember(exercise) { mutableStateOf(exercise?.description ?: "") }

    val exerciseTypes = listOf("Силовое", "Кардио", "Растяжка", "Функциональное")
    var selectedType by remember { mutableStateOf("Силовое") }

    LaunchedEffect(exerciseId) {
        if (exercise == null && exerciseId.isNotBlank()) {
            // TODO: load by id
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "",
                navController = navController,
                containerColor = Background
            )
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
            // График
            item {
                Card(padding = 0.dp, shapeRadius = 16.dp) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "График",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                "Наибольший: 98кг",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                "Наименьший: 60.5кг",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Название
            item {
                Text(
                    "Название упражнения",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    value = name,
                    onValueChange = { name = it }
                )
            }

            // Группы мышц
            item {
                Text(
                    "Группы мышц",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MuscleGroup.entries.forEach { group ->
                        Chip(
                            text = group.displayName,
                            isSelected = selectedMuscleGroups.contains(group),
                            onClick = {
                                selectedMuscleGroups = if (selectedMuscleGroups.contains(group)) {
                                    selectedMuscleGroups - group
                                } else {
                                    selectedMuscleGroups + group
                                }
                            }
                        )
                    }
                }
            }

            // Тип
            item {
                Text(
                    "Тип упражнения",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    exerciseTypes.forEach { type ->
                        Chip(
                            text = type,
                            isSelected = type == selectedType,
                            onClick = { selectedType = type }
                        )
                    }
                }
            }

            // Описание
            item {
                Text(
                    "Описание упражнения",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Введите описание",
                    minLines = 4,
                    singleLine = false
                )
            }

            // Сохранить
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    text = "Сохранить изменения",
                    onClick = {
                        viewModel.updateTemplate(exerciseId, name, description, selectedMuscleGroups)
                        navController.navigateUp()
                    },
                    isPrimary = false
                )
            }

            // Удалить
            if (exerciseId.isNotBlank()) {
                item {
                    DangerButton(
                        text = "Удалить упражнение",
                        onClick = {
                            viewModel.deleteTemplate(exerciseId)
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}
