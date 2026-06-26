package com.example.presentation.workout.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.domain.model.UserRole
import com.example.domain.model.workout.MuscleGroup
import com.example.presentation.auth.AuthViewModel
import com.example.presentation.ui.components.Button
import com.example.presentation.ui.components.Card
import com.example.presentation.ui.components.Chip
import com.example.presentation.ui.components.DangerButton
import com.example.presentation.ui.components.TextField
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.workout.viewmodels.ExerciseTemplatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    val authViewModel: AuthViewModel = hiltViewModel()
    val userRole by authViewModel.userRole.collectAsStateWithLifecycle()
    val isGuest = userRole == UserRole.GUEST

    var name by remember(exercise) { mutableStateOf(exercise?.name ?: "") }
    var selectedMuscleGroups by remember(exercise) { mutableStateOf(exercise?.muscleGroups ?: emptyList()) }
    var description by remember(exercise) { mutableStateOf(exercise?.description ?: "") }

    val exerciseTypes = listOf("Силовое")
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
            // Название (только чтение для гостя)
            item {
                Text(
                    "Название упражнения",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                if (isGuest) {
                    Text(
                        text = name,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    TextField(
                        value = name,
                        onValueChange = { name = it }
                    )
                }
            }

            // Группы мышц
            item {
                Text(
                    "Группы мышц",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (isGuest) {
                    Text(
                        text = selectedMuscleGroups.joinToString(", ") { it.displayName },
                        color = TextPrimary,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            }

            // Тип
            item {
                Text(
                    "Тип упражнения",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (isGuest) {
                    Text(
                        text = selectedType,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            }

            // Описание
            item {
                Text(
                    "Описание упражнения",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                if (isGuest) {
                    Text(
                        text = description.ifBlank { "Нет описания" },
                        color = if (description.isBlank()) TextSecondary else TextPrimary,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "Введите описание",
                        minLines = 4,
                        singleLine = false
                    )
                }
            }

            // Сохранить / Удалить — только для пользователей
            if (!isGuest) {
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
}