package com.example.presentation.meal.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import com.example.presentation.ui.components.Button
import com.example.presentation.ui.components.TextField
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodItemScreen(
    navController: NavController
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Новый продукт",
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
                Text(
                    "Название продукта",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Введите название"
                )
            }

            item {
                Text(
                    "Калорийность",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    value = calories,
                    onValueChange = { calories = it },
                    placeholder = "Ккал на 100г"
                )
            }

            item {
                Text(
                    "Белки",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    value = protein,
                    onValueChange = { protein = it },
                    placeholder = "Грамм на 100г"
                )
            }

            item {
                Text(
                    "Жиры",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    value = fats,
                    onValueChange = { fats = it },
                    placeholder = "Грамм на 100г"
                )
            }

            item {
                Text(
                    "Углеводы",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                TextField(
                    value = carbs,
                    onValueChange = { carbs = it },
                    placeholder = "Грамм на 100г"
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    text = "Добавить продукт",
                    onClick = { /* Save product */ }
                )
            }
        }
    }
}
