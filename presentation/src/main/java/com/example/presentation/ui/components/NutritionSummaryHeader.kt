package com.example.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary

@Composable
fun NutritionSummaryHeader(
    currentCalories: Int,
    goalCalories: Int,
    currentMacros: MacroValues,
    goalMacros: MacroValues,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Левая колонка - Сегодня
        Column {
            Text(
                text = "Сегодня",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$currentCalories ккал",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "${currentMacros.carbs.toInt()}-${currentMacros.protein.toInt()}Б-${currentMacros.fat.toInt()}Ж",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Правая колонка - Цель
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Цель",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$goalCalories ккал",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "${goalMacros.carbs.toInt()}-${goalMacros.protein.toInt()}Б-${goalMacros.fat.toInt()}Ж",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

data class MacroValues(
    val carbs: Double,
    val protein: Double,
    val fat: Double
)
