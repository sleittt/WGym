package com.example.presentation.workout.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.domain.model.workout.MuscleGroup
import com.example.presentation.ui.components.Button
import com.example.presentation.ui.components.Chip
import com.example.presentation.ui.components.TextField
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.workout.viewmodels.ExerciseTemplatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCreateScreen(
    navController: NavController,
    viewModel: ExerciseTemplatesViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var selectedMuscleGroups by remember { mutableStateOf(listOf<MuscleGroup>()) }
    var selectedType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val exerciseTypes = listOf("Силовое")
    val scrollState = rememberScrollState()

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text("Название упражнения", color = TextSecondary, fontSize = 14.sp)
            TextField(
                value = name,
                onValueChange = { name = it }
            )

            Text("Группы мышц", color = TextSecondary, fontSize = 14.sp)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val rows = MuscleGroup.entries.chunked(3)
                rows.forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowItems.forEach { group ->
                            val selected = selectedMuscleGroups.contains(group)
                            Chip(
                                text = group.displayName,
                                isSelected = selected,
                                onClick = {
                                    selectedMuscleGroups = if (selected) {
                                        selectedMuscleGroups - group
                                    } else {
                                        selectedMuscleGroups + group
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Text("Тип упражнения", color = TextSecondary, fontSize = 14.sp)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                exerciseTypes.forEach { type ->
                    Chip(
                        text = type,
                        isSelected = type == selectedType,
                        onClick = { selectedType = type },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Text("Описание упражнения", color = TextSecondary, fontSize = 14.sp)
            TextField(
                value = description,
                onValueChange = { description = it },
                placeholder = "Введите описание",
                minLines = 4,
                singleLine = false
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                text = "Добавить упражнение",
                onClick = {
                    viewModel.createTemplate(name, description, selectedMuscleGroups)
                    navController.navigateUp()
                },
                isPrimary = false
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
