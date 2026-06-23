package com.example.presentation.meal.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.domain.model.meal.MealType
import com.example.presentation.meal.viewmodels.MealItemDetailViewModel
import com.example.presentation.ui.components.Button
import com.example.presentation.ui.components.Chip
import com.example.presentation.ui.components.DangerButton
import com.example.presentation.ui.components.TextField
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealItemDetailScreen(
    navController: NavController,
    viewModel: MealItemDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Читаем параметры из savedStateHandle предыдущего экрана
    val foodItemId = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("meal_item_food_id") ?: ""
    val mealItemId = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("meal_item_id")
    val initialMealType = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("meal_item_meal_type")
    val initialAmount = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<Float>("meal_item_amount")
    val dateString = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("meal_item_date")

    // Загружаем данные при первом открытии
    androidx.compose.runtime.LaunchedEffect(foodItemId) {
        if (foodItemId.isNotBlank()) {
            viewModel.load(foodItemId, mealItemId, initialMealType, initialAmount, dateString)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Заголовок - название продукта
            Text(
                text = uiState.foodItemName.ifBlank { "Продукт" },
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Поле Грамм
            FieldLabel("Грамм")
            TextField(
                value = uiState.amountGrams,
                onValueChange = viewModel::onAmountChange,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Время приёма (чипы)
            FieldLabel("Время приёма")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MealType.entries.forEach { mealType ->
                    val title = when (mealType) {
                        MealType.BREAKFAST -> "Утро"
                        MealType.LUNCH -> "День"
                        MealType.DINNER -> "Вечер"
                        MealType.SNACK -> "Перекус"
                    }
                    Chip(
                        text = title,
                        isSelected = uiState.selectedMealType == mealType,
                        onClick = { viewModel.onMealTypeChange(mealType) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопка удалить (только при редактировании существующей записи)
            if (uiState.isEditing) {
                DangerButton(
                    text = "Удалить запись",
                    onClick = { viewModel.onDelete { navController.navigateUp() } },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Кнопка добавить/сохранить
            Button(
                text = if (uiState.isEditing) "Сохранить" else "Добавить запись",
                onClick = {
                    viewModel.onSave { navController.navigateUp() }
                },
                enabled = uiState.isValid,
                isPrimary = false,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = TextSecondary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
