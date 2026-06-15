package com.example.presentation.meal.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.domain.model.meal.FoodItem
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.Surface
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.meal.viewmodels.FoodItemsViewModel


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
                title = {
                    Text(
                        "Продукты",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1C1E)
                )
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
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        },
        containerColor = Color(0xFF1C1C1E)
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
                    unfocusedContainerColor = Surface,
                    focusedPlaceholderColor = TextSecondary,
                    unfocusedPlaceholderColor = TextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    uiState.foodItems.filter {
                        it.name.contains(uiState.searchQuery, ignoreCase = true)
                    }
                ) { item ->
                    FoodItemListCard(
                        item = item,
                        onEdit = { viewModel.selectFoodItem(item) },
                        onDelete = { viewModel.deleteFoodItemById(item.id)
                        }
                    )
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Редактировать",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = PrimaryRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Macros row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroBadge("К", item.caloriesPer100g.toInt().toString(), Color(0xFFFF9500))
                MacroBadge("Б", "${item.proteinPer100g.toInt()}г", Color(0xFF34C759))
                MacroBadge("Ж", "${item.fatsPer100g.toInt()}г", Color(0xFFFF3B30))
                MacroBadge("У", "${item.carbsPer100g.toInt()}г", Color(0xFF5AC8FA))
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
fun MacroBadge(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "$label: $value",
            color = TextSecondary,
            fontSize = 13.sp
        )
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
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Название", color = TextSecondary) },
                    colors = textFieldColors(), shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Описание", color = TextSecondary) },
                    colors = textFieldColors(), shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    OutlinedTextField(
                        value = calories, onValueChange = { calories = it },
                        label = { Text("Ккал", color = TextSecondary) },
                        colors = textFieldColors(), shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = serving, onValueChange = { serving = it },
                        label = { Text("Порция", color = TextSecondary) },
                        colors = textFieldColors(), shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    OutlinedTextField(
                        value = protein, onValueChange = { protein = it },
                        label = { Text("Белки", color = TextSecondary) },
                        colors = textFieldColors(), shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = fats, onValueChange = { fats = it },
                        label = { Text("Жиры", color = TextSecondary) },
                        colors = textFieldColors(), shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = carbs, onValueChange = { carbs = it },
                        label = { Text("Углеводы", color = TextSecondary) },
                        colors = textFieldColors(), shape = RoundedCornerShape(12.dp),
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

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = PrimaryRed,
    unfocusedBorderColor = SurfaceVariant,
    focusedContainerColor = SurfaceVariant,
    unfocusedContainerColor = SurfaceVariant
)
