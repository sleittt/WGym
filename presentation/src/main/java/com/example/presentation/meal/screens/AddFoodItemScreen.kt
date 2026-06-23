package com.example.presentation.meal.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.presentation.meal.viewmodels.AddFoodItemViewModel
import com.example.presentation.ui.components.Button
import com.example.presentation.ui.components.DangerButton
import com.example.presentation.ui.components.TextField
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodItemScreen(
    navController: NavController,
    viewModel: AddFoodItemViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Проверяем, есть ли id для редактирования
    val editFoodItemId = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("edit_food_item_id")

    androidx.compose.runtime.LaunchedEffect(editFoodItemId) {
        editFoodItemId?.let { viewModel.loadForEdit(it) }
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
            Spacer(modifier = Modifier.height(8.dp))

            // Название продукта
            FieldLabel("Название продукта")
            TextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Калорийность
            FieldLabel("Калорийность")
            TextField(
                value = uiState.calories,
                onValueChange = viewModel::onCaloriesChange,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Белки
            FieldLabel("Белки")
            TextField(
                value = uiState.protein,
                onValueChange = viewModel::onProteinChange,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Жиры
            FieldLabel("Жиры")
            TextField(
                value = uiState.fat,
                onValueChange = viewModel::onFatChange,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Углеводы
            FieldLabel("Углеводы")
            TextField(
                value = uiState.carbs,
                onValueChange = viewModel::onCarbsChange,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Кнопка удалить (только при редактировании)
            if (uiState.isEditing) {
                DangerButton(
                    text = "Удалить запись",
                    onClick = {
                        viewModel.onDelete { navController.navigateUp() }
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Кнопка добавить/сохранить
            Button(
                text = if (uiState.isEditing) "Сохранить" else "Добавить продукт",
                onClick = {
                    viewModel.onAddClick(
                        onSuccess = { navController.navigateUp() },
                        onError = { /* TODO: show error */ }
                    )
                },
                enabled = uiState.isValid,
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
