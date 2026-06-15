package com.example.presentation.workout.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.presentation.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExerciseToWorkoutScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var selectedMuscleGroups by remember { mutableStateOf(listOf<String>()) }
    var selectedType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val muscleGroups = listOf("Грудь", "Спина", "Ноги", "Плечи", "Бицепс", "Трицепс", "Пресс")
    val exerciseTypes = listOf("Силовое", "Кардио", "Растяжка", "Функциональное")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
            item {
                Text("Название упражнения", color = TextSecondary, fontSize = 14.sp)
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    colors = textFieldColors(), shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Text("Группы мышц", color = TextSecondary, fontSize = 14.sp)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    muscleGroups.forEach { group ->
                        val selected = selectedMuscleGroups.contains(group)
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                .background(if (selected) PrimaryRed else SurfaceVariant)
                                .clickable {
                                    selectedMuscleGroups = if (selected) selectedMuscleGroups - group else selectedMuscleGroups + group
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(group, color = if (selected) Color.White else TextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            }
            item {
                Text("Тип упражнения", color = TextSecondary, fontSize = 14.sp)
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant).padding(16.dp)
                ) {
                    Text("Выберите тип", color = TextSecondary, fontSize = 16.sp)
                }
            }
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    exerciseTypes.forEach { type ->
                        val selected = type == selectedType
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                .background(if (selected) PrimaryRed else SurfaceVariant)
                                .clickable { selectedType = type }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(type, color = if (selected) Color.White else TextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            }
            item {
                Text("Описание упражнения", color = TextSecondary, fontSize = 14.sp)
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    placeholder = { Text("Введите описание", color = TextSecondary) },
                    colors = textFieldColors(), shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant).clickable { }.padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Добавить упражнение", color = TextPrimary, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
    focusedBorderColor = PrimaryRed, unfocusedBorderColor = SurfaceVariant,
    focusedContainerColor = SurfaceVariant, unfocusedContainerColor = SurfaceVariant
)
