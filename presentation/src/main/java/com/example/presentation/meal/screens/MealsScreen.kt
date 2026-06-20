package com.example.presentation.meal.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.domain.model.meal.MealType
import com.example.presentation.ui.components.Card
import com.example.presentation.ui.components.CircularProgress
import com.example.presentation.ui.components.EmptyListState
import com.example.presentation.ui.components.LoadingIndicator
import com.example.presentation.meal.viewmodels.MealsViewModel
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.Surface
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsScreen(
    navController: NavController,
    viewModel: MealsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPeriod by remember { mutableStateOf("День") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Питание",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceVariant)
                                .clickable { /* Show period selector */ }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    selectedPeriod,
                                    color = TextPrimary,
                                    fontSize = 16.sp
                                )
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
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
                    containerColor = Background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add food entry */ },
                containerColor = PrimaryRed,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        },
        containerColor = Background
    ) { padding ->
        if (uiState.isLoading && uiState.meals.isEmpty()) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(padding)
            ) {
                // Nutrition summary with REAL calculated data
                item {
                    NutritionDaySummaryCard(
                        currentCalories = uiState.dailySummary.calories.toInt(),
                        goalCalories = uiState.calorieGoal,
                        protein = uiState.dailySummary.protein.toFloat(),
                        proteinGoal = uiState.proteinGoal,
                        fat = uiState.dailySummary.fats.toFloat(),
                        fatGoal = uiState.fatGoal,
                        carbs = uiState.dailySummary.carbs.toFloat(),
                        carbsGoal = uiState.carbsGoal
                    )
                }

                // Period selector
                item {
                    PeriodSelector(
                        selected = selectedPeriod,
                        onSelect = { selectedPeriod = it }
                    )
                }

                // Meal cards with real data
                MealType.entries.forEach { mealType ->
                    val meal = uiState.meals.find { it.type == mealType }
                    item {
                        MealCard(
                            mealType = mealType,
                            meal = meal,
                            foodItems = uiState.foodItems,
                            onAddFood = { viewModel.openAddFoodDialog(mealType) },
                            onRemoveFood = { foodItemId ->
                                meal?.let { viewModel.removeFood(it.id, foodItemId) }
                            }
                        )
                    }
                }

                // Add entry button
                item {
                    EmptyListState(
                        text = "Добавить запись",
                        onClick = { /* Add entry */ }
                    )
                }
            }
        }
    }
}

@Composable
fun NutritionDaySummaryCard(
    currentCalories: Int,
    goalCalories: Int,
    protein: Float,
    proteinGoal: Float,
    fat: Float,
    fatGoal: Float,
    carbs: Float,
    carbsGoal: Float
) {
    Card(
        shapeRadius = 20.dp,
        padding = 20.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left - consumed
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Сегодня", color = TextSecondary, fontSize = 14.sp)
                Text(
                    "$currentCalories ккал",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${protein.toInt()}г-${fat.toInt()}г-${carbs.toInt()}г",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            // Center - circular progress
            CircularProgress(
                current = currentCalories,
                goal = goalCalories,
                size = 100.dp,
                strokeWidth = 8.dp,
                progressColor = PrimaryRed
            )

            // Right - goal
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Цель", color = TextSecondary, fontSize = 14.sp)
                Text(
                    "$goalCalories ккал",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${proteinGoal.toInt()}г-${fatGoal.toInt()}г-${carbsGoal.toInt()}г",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun PeriodSelector(
    selected: String,
    onSelect: (String) -> Unit
) {
    val periods = listOf("День", "Неделя", "Месяц")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        periods.forEach { period ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (period == selected) SurfaceVariant else Surface)
                    .clickable { onSelect(period) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    period,
                    color = if (period == selected) TextPrimary else TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = if (period == selected) FontWeight.Medium else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun MealCard(
    mealType: MealType,
    meal: com.example.domain.model.meal.Meal?,
    foodItems: Map<String, com.example.domain.model.meal.FoodItem>,
    onAddFood: () -> Unit,
    onRemoveFood: (String) -> Unit
) {
    val (title, icon) = when (mealType) {
        MealType.BREAKFAST -> Pair("Утро", "🌅")
        MealType.LUNCH -> Pair("День", "☀️")
        MealType.DINNER -> Pair("Вечер", "🌙")
        MealType.SNACK -> Pair("Перекус", "🍎")
    }

    // Calculate meal calories from real data
    val mealCalories = meal?.items?.sumOf { item ->
        val food = foodItems[item.foodItemId]
        food?.let { it.caloriesPer100g * (item.amountGrams / 100.0) } ?: 0.0
    }?.toInt() ?: 0

    Card {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        title,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (mealCalories > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "$mealCalories ккал",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(12.dp))

            if (meal != null && meal.items.isNotEmpty()) {
                meal.items.forEach { item ->
                    val food = foodItems[item.foodItemId]
                    val itemCalories = food?.let {
                        (it.caloriesPer100g * (item.amountGrams / 100.0)).toInt()
                    } ?: 0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                food?.name ?: item.foodItemId,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                            Text(
                                "${item.amountGrams.toInt()} грамм",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "$itemCalories ккал",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            } else {
                Text(
                    "Нет продуктов",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
