package com.example.presentation.meal.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.presentation.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodToMealScreen(navController: NavController) {
    var grams by remember { mutableStateOf("") }
    var selectedMealTime by remember { mutableStateOf("Вечер") }
    val mealTimes = listOf("Утро", "День", "Вечер")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Продукт", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
                Text("Грамм", color = TextSecondary, fontSize = 14.sp)
                OutlinedTextField(
                    value = grams, onValueChange = { grams = it },
                    placeholder = { Text("Введите количество", color = TextSecondary) },
                    colors = textFieldColors(), shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Text("Время приема", color = TextSecondary, fontSize = 14.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    mealTimes.forEach { time ->
                        val selected = time == selectedMealTime
                        Box(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                                .background(if (selected) PrimaryRed else SurfaceVariant)
                                .clickable { selectedMealTime = time }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(time, color = if (selected) Color.White else TextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(PrimaryRed).clickable { }.padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Удалить запись", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant).clickable { }.padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Добавить запись", color = TextPrimary, fontSize = 16.sp)
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
