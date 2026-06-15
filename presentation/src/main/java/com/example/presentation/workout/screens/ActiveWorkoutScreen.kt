package com.example.presentation.workout.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryGreen
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.Surface
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    navController: NavController,
    templateName: String = "Имя шаблона"
) {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableStateOf("67:67") } // From Figma

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Timer in the center like Figma
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            elapsedTime,
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
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
                actions = {
                    IconButton(onClick = { /* Finish workout */ }) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Завершить",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(28.dp)
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
                onClick = { /* Add exercise */ },
                containerColor = PrimaryRed,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить упражнение")
            }
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
            // Exercise cards with sets
            items(3) { index ->
                ExerciseCardWithSets(
                    exerciseName = "Упражнение",
                    sets = listOf(
                        SetData("W", "12 кг x 12", "12 кг", "12"),
                        SetData("1", "12 кг x 12", "12 кг", "12", isCompleted = true, restTime = "67:67"),
                        SetData("2", "12 кг x 12", "12 кг", "12", restTime = "67:67"),
                        SetData("F", "12 кг x 12", "12 кг", "12")
                    )
                )
            }

            // Add exercise button at bottom
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant)
                        .clickable { /* Add exercise */ }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Добавить упражнение",
                        color = TextPrimary,
                        fontSize = 16.sp
                    )
                }
            }

            // Save template button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariant)
                        .clickable { /* Save template */ }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Сохранить шаблон",
                        color = TextPrimary,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

data class SetData(
    val setNumber: String,
    val previous: String,
    val weight: String,
    val reps: String,
    val isCompleted: Boolean = false,
    val restTime: String = ""
)

@Composable
fun ExerciseCardWithSets(
    exerciseName: String,
    sets: List<SetData>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Exercise name
            Text(
                exerciseName,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Подход", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(50.dp))
                Text("Пред.", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Вес", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Повторы", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(60.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sets rows
            sets.forEach { set ->
                SetRow(
                    setData = set,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun SetRow(
    setData: SetData,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        setData.isCompleted -> PrimaryGreen.copy(alpha = 0.2f)
        setData.setNumber == "W" -> SurfaceVariant
        else -> SurfaceVariant.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { /* Toggle set completion */ }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Set number
            Text(
                setData.setNumber,
                color = if (setData.isCompleted) PrimaryGreen else TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(50.dp)
            )

            // Previous
            Text(
                setData.previous,
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )

            // Weight
            Text(
                setData.weight,
                color = TextPrimary,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            // Reps
            Text(
                setData.reps,
                color = TextPrimary,
                fontSize = 14.sp,
                modifier = Modifier.width(60.dp)
            )
        }
    }

    // Rest time indicator (green line)
    if (setData.restTime.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(PrimaryGreen)
        )
        Text(
            setData.restTime,
            color = PrimaryGreen,
            fontSize = 11.sp,
            modifier = Modifier.padding(start = 50.dp, top = 2.dp)
        )
    }
}
