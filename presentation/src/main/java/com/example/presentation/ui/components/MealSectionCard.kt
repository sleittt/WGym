package com.example.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.ui.theme.TextPrimary

@Composable
fun MealSectionCard(
    title: String,
    items: List<MealItemUi>,
    onAddClick: () -> Unit,
    onItemClick: (MealItemUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        padding = 0.dp,
        shapeRadius = 16.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Заголовок секции
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier.padding(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить",
                        tint = TextPrimary,
                        modifier = Modifier.padding(0.dp)
                    )
                }
            }

            // Список продуктов
            if (items.isEmpty()) {
                EmptyListState(
                    text = "Добавить продукт",
                    onClick = onAddClick,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            } else {
                items.forEachIndexed { index, item ->
                    MealItemRow(
                        name = item.name,
                        amountGrams = item.amountGrams,
                        calories = item.calories,
                        onClick = { onItemClick(item) },
                        showDivider = index < items.size - 1
                    )
                }
            }
        }
    }
}

data class MealItemUi(
    val id: String,
    val foodItemId: String,
    val name: String,
    val amountGrams: Float,
    val calories: Int,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)
