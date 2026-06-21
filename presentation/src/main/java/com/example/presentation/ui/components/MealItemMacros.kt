package com.example.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MealItemMacros(
    protein: Double,
    fat: Double,
    carbs: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MacroBadge(
            label = "Б",
            value = "${protein.toInt()}г",
            color = MacroColors.Protein
        )
        MacroBadge(
            label = "Ж",
            value = "${fat.toInt()}г",
            color = MacroColors.Fat
        )
        MacroBadge(
            label = "У",
            value = "${carbs.toInt()}г",
            color = MacroColors.Carbs
        )
    }
}
