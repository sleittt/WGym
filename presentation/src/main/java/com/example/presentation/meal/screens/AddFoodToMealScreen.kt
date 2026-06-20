package com.example.presentation.meal.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.navigation.NavController
import com.example.presentation.ui.components.Chip
import com.example.presentation.ui.components.DangerButton
import com.example.presentation.ui.components.SecondaryButton
import com.example.presentation.ui.components.TextField
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodToMealScreen(navController: NavController) {
    var grams by remember { mutableStateOf("") }
    var selectedMealTime by remember { mutableStateOf("Вечер") }
    val mealTimes = listOf("Утро", "День", "Вечер")

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Продукт",
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
            item {
                Text("Грамм", color = TextSecondary, fontSize = 14.sp)
                TextField(
                    value = grams,
                    onValueChange = { grams = it },
                    placeholder = "Введите количество"
                )
            }
            item {
                Text("Время приема", color = TextSecondary, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    mealTimes.forEach { time ->
                        Chip(
                            text = time,
                            isSelected = time == selectedMealTime,
                            onClick = { selectedMealTime = time },
                            modifier = Modifier.weight(1f),
                            cornerRadius = 10.dp,
                            horizontalPadding = 0.dp,
                            verticalPadding = 12.dp
                        )
                    }
                }
            }
            item {
                DangerButton(
                    text = "Удалить запись",
                    onClick = { }
                )
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
                SecondaryButton(
                    text = "Добавить запись",
                    onClick = { }
                )
            }
        }
    }
}
