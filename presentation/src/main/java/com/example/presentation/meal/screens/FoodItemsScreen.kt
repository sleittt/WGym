package com.example.presentation.meal.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.presentation.navigation.Screen
import com.example.presentation.ui.components.LoadingIndicator
import com.example.presentation.ui.components.TextField
import com.example.presentation.ui.components.TopAppBar
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
    selectMode: Boolean = false,
    mealType: String? = null,
    date: String? = null,
    viewModel: FoodItemsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = if (selectMode) "Выберите продукт" else "Продукты",
                navController = navController,
                containerColor = Background
            )
        },
        floatingActionButton = {
            if (!selectMode) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddFoodItem.route) },
                    containerColor = PrimaryRed,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить продукт")
                }
            }
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Поисковая строка
            TextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.search(it)
                },
                placeholder = "Поиск",
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Контент
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.isLoading && uiState.filteredItems.isEmpty()) {
                    LoadingIndicator()
                } else {
                    val filtered = uiState.filteredItems.filter {
                        it.name.contains(searchQuery, ignoreCase = true)
                    }

                    if (filtered.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Нет продуктов",
                                color = TextSecondary,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Секция "Последнее" - если есть recently used
                            val recent = uiState.recentItems.filter { 
                                it.name.contains(searchQuery, ignoreCase = true) 
                            }
                            if (recent.isNotEmpty()) {
                                item {
                                    Text(
                                        "Последнее: Утро",
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                items(recent) { item ->
                                    FoodItemListRow(
                                        item = item,
                                        selectMode = selectMode,
                                        onClick = {
                                            if (selectMode) {
                                                navController.previousBackStackEntry
                                                    ?.savedStateHandle
                                                    ?.set("selected_food_item_id", item.id)
                                                navController.navigateUp()
                                            } else {
                                                navController.navigate(
                                                    Screen.FoodItems.route.replace("food_items", "food_item_detail/${item.id}")
                                                )
                                            }
                                        },
                                        onAdd = {
                                            navController.previousBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("selected_food_item_id", item.id)
                                            navController.navigateUp()
                                        }
                                    )
                                }
                            }

                            // Секция "Остальное"
                            val other = filtered.filter { !recent.contains(it) }
                            if (other.isNotEmpty()) {
                                item {
                                    Text(
                                        if (recent.isNotEmpty()) "Остальное" else "",
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                items(other) { item ->
                                    FoodItemListRow(
                                        item = item,
                                        selectMode = selectMode,
                                        onClick = {
                                            if (selectMode) {
                                                navController.previousBackStackEntry
                                                    ?.savedStateHandle
                                                    ?.set("selected_food_item_id", item.id)
                                                navController.navigateUp()
                                            } else {
                                                navController.navigate(
                                                    Screen.FoodItems.route.replace("food_items", "food_item_detail/${item.id}")
                                                )
                                            }
                                        },
                                        onAdd = {
                                            navController.previousBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("selected_food_item_id", item.id)
                                            navController.navigateUp()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Кнопка "Добавить продукт" + камера
            if (!selectMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceVariant)
                            .clickable { navController.navigate(Screen.AddFoodItem.route) }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Добавить продукт",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceVariant)
                            .clickable { /* TODO: Camera/Scanner */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Камера",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FoodItemListRow(
    item: FoodItem,
    selectMode: Boolean,
    onClick: () -> Unit,
    onAdd: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.name,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${item.caloriesPer100g.toInt()} ккал - ${item.proteinPer100g.toInt()}Б-${item.fatsPer100g.toInt()}Ж-${item.carbsPer100g.toInt()}У",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "${item.caloriesPer100g.toInt()} ккал",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                if (selectMode) {
                    IconButton(
                        onClick = onAdd,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить",
                            tint = PrimaryRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
