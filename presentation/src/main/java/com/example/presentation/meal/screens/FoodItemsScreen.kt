package com.example.presentation.meal.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.domain.model.meal.FoodItem
import com.example.presentation.ui.components.Card
import com.example.presentation.ui.components.DropdownMenu
import com.example.presentation.ui.components.LoadingIndicator
import com.example.presentation.ui.components.MacroBadge
import com.example.presentation.ui.components.MacroColors
import com.example.presentation.meal.viewmodels.FoodItemsViewModel
import com.example.presentation.ui.components.DropdownAction
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.components.TextField
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.Surface
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodItemsScreen(
    navController: NavController,
    viewModel: FoodItemsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Продукты",
                navController = navController,
                containerColor = Background
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.selectFoodItem(
                        FoodItem(
                            id = "",
                            name = "",
                            caloriesPer100g = 0.0,
                            proteinPer100g = 0.0,
                            fatsPer100g = 0.0,
                            carbsPer100g = 0.0
                        )
                    )
                },
                containerColor = PrimaryRed,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        },
        containerColor = Background
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Поиск продуктов...", color = TextSecondary) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PrimaryRed,
                    unfocusedBorderColor = SurfaceVariant,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (uiState.isLoading && uiState.filteredItems.isEmpty()) {
                LoadingIndicator()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.filteredItems) { item ->
                        FoodItemListCard(
                            item = item,
                            onEdit = { viewModel.selectFoodItem(item) },
                            onDelete = { viewModel.deleteFoodItemById(item.id) }
                        )
                    }
                }
            }
        }

        if (uiState.isEditDialogOpen) {
            FoodItemEditDialog(
                item = uiState.selectedFoodItem,
                onDismiss = { viewModel.dismissDialog() },
                onConfirm = { name, description, calories, protein, fats, carbs, serving ->
                    if (uiState.selectedFoodItem?.id.isNullOrBlank()) {
                        viewModel.createFoodItem(name, description, calories, protein, fats, carbs, serving)
                    } else {
                        viewModel.updateFoodItem(
                            uiState.selectedFoodItem!!.id,
                            name, description, calories, protein, fats, carbs, serving
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun FoodItemListCard(
    item: FoodItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.name,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (item.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            item.description,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }

                DropdownMenu(
                    actions = listOf(
                        DropdownAction("Редактировать", onClick = onEdit),
                        DropdownAction("Удалить", isDanger = true, onClick = onDelete)
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Macros row with real data
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroBadge("К", "${item.caloriesPer100g.toInt()}", MacroColors.Calories)
                MacroBadge("Б", "${item.proteinPer100g.toInt()}г", MacroColors.Protein)
                MacroBadge("Ж", "${item.fatsPer100g.toInt()}г", MacroColors.Fat)
                MacroBadge("У", "${item.carbsPer100g.toInt()}г", MacroColors.Carbs)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Порция: ${item.servingDefaultGrams.toInt()}г",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun FoodItemEditDialog(
    item: FoodItem?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, Double, Double, Double, Double) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var description by remember { mutableStateOf(item?.description ?: "") }
    var calories by remember { mutableStateOf(item?.caloriesPer100g?.toString() ?: "") }
    var protein by remember { mutableStateOf(item?.proteinPer100g?.toString() ?: "") }
    var fats by remember { mutableStateOf(item?.fatsPer100g?.toString() ?: "") }
    var carbs by remember { mutableStateOf(item?.carbsPer100g?.toString() ?: "") }
    var serving by remember { mutableStateOf(item?.servingDefaultGrams?.toString() ?: "100") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Text(
                if (item?.id.isNullOrBlank()) "Новый продукт" else "Редактировать продукт",
                color = TextPrimary
            )
        },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Название"
                )
                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Описание"
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    TextField(
                        value = calories,
                        onValueChange = { calories = it },
                        label = "Ккал",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    TextField(
                        value = serving,
                        onValueChange = { serving = it },
                        label = "Порция",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    TextField(
                        value = protein,
                        onValueChange = { protein = it },
                        label = "Белки",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    TextField(
                        value = fats,
                        onValueChange = { fats = it },
                        label = "Жиры",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    TextField(
                        value = carbs,
                        onValueChange = { carbs = it },
                        label = "Углеводы",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        name, description,
                        calories.toDoubleOrNull() ?: 0.0,
                        protein.toDoubleOrNull() ?: 0.0,
                        fats.toDoubleOrNull() ?: 0.0,
                        carbs.toDoubleOrNull() ?: 0.0,
                        serving.toDoubleOrNull() ?: 100.0
                    )
                }
            ) { Text("Сохранить", color = PrimaryRed) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена", color = TextSecondary) }
        }
    )
}
