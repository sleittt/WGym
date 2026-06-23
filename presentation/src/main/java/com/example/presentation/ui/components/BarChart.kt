package com.example.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.ui.theme.PrimaryGreen
import com.example.presentation.ui.theme.PrimaryOrange
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary

@Composable
fun BarChart(
    bars: List<BarData>,
    onBarClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    barColor: Color = PrimaryGreen,
    emptyColor: Color = SurfaceVariant,
    chartHeight: Dp = 180.dp,
    barWidth: Dp = 28.dp,
    maxValue: Float? = null
) {
    val values = bars.map { it.value }
    val calculatedMax = maxValue ?: values.maxOrNull()?.coerceAtLeast(1f) ?: 1f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(chartHeight)
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            bars.forEachIndexed { index, bar ->
                val fraction = if (calculatedMax > 0) {
                    (bar.value / calculatedMax).coerceIn(0f, 1f)
                } else 0f

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(barWidth)
                ) {
                    // Значение над столбцом
                    if (bar.value > 0) {
                        Text(
                            text = "${bar.value.toInt()}",
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Столбец
                    Box(
                        modifier = Modifier
                            .width(barWidth)
                            .fillMaxHeight(fraction)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(if (bar.value > 0) barColor else emptyColor)
                            .clickable { onBarClick(index) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Подписи
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            bars.forEach { bar ->
                Text(
                    text = bar.label,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(barWidth)
                )
            }
        }
    }
}

data class BarData(
    val label: String,
    val value: Float,
    val dateKey: String = "" // для навигации
)
