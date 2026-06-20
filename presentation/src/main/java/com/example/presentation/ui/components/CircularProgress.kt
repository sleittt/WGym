package com.example.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary

@Composable
fun CircularProgress(
    current: Number,
    goal: Number,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 6.dp,
    progressColor: Color = Color.Red,
    trackColor: Color = SurfaceVariant,
    strokeCap: StrokeCap = StrokeCap.Round,
    centerContent: @Composable () -> Unit = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                current.toString(),
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
) {
    val progress = if (goal.toFloat() > 0) {
        (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(size),
            color = progressColor,
            trackColor = trackColor,
            strokeWidth = strokeWidth,
            strokeCap = strokeCap
        )
        centerContent()
    }
}

@Composable
fun DualCircularProgress(
    leftCurrent: Number,
    leftGoal: Number,
    rightCurrent: Number,
    rightGoal: Number,
    leftColor: Color,
    rightColor: Color,
    modifier: Modifier = Modifier,
    centerContent: @Composable () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgress(
            current = leftCurrent,
            goal = leftGoal,
            progressColor = leftColor,
            size = 80.dp,
            strokeWidth = 6.dp
        )

        centerContent()

        CircularProgress(
            current = rightCurrent,
            goal = rightGoal,
            progressColor = rightColor,
            size = 80.dp,
            strokeWidth = 6.dp
        )
    }
}
