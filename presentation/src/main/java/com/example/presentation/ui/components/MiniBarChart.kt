package com.example.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.ui.theme.PrimaryGreen
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary

@Composable
fun MiniBarChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    barColor: Color = PrimaryGreen,
    emptyColor: Color = SurfaceVariant,
    maxBars: Int = 7,
    barWidth: Dp = 8.dp,
    chartHeight: Dp = 60.dp
) {
    val displayValues = if (values.size > maxBars) values.takeLast(maxBars) else values
    val maxValue = displayValues.maxOrNull()?.coerceAtLeast(1f) ?: 1f

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            displayValues.forEach { value ->
                val fraction = (value / maxValue).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .width(barWidth)
                        .fillMaxHeight(fraction)
                        .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                        .background(if (value > 0) barColor else emptyColor)
                )
            }
        }
    }
}

@Composable
fun MiniChartPlaceholder(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Light
        )
    }
}
