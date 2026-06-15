package com.example.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.presentation.navigation.Screen
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryGreen
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.Surface
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.workout.viewmodels.WorkoutTemplatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: WorkoutTemplatesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "WGym",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Section 1: Workout Templates Grid (2x2) - exactly like Figma
            Text(
                "Шаблоны тренировок",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(280.dp)
            ) {
                items(uiState.templates.take(4)) { template ->
                    WorkoutTemplateHomeCard(
                        name = template.name,
                        onPlay = { /* Start workout */ },
                        onEdit = {
                            navController.navigate(Screen.WorkoutTemplates.route)
                        }
                    )
                }
                // Fill empty slots if less than 4 templates
                if (uiState.templates.size < 4) {
                    items(4 - uiState.templates.size) {
                        EmptyTemplateCard(
                            onClick = { navController.navigate(Screen.WorkoutTemplates.route) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Calories Progress - exactly like Figma with two rings
            CaloriesProgressCard(
                current = 1300,
                goal = 2000
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section 3: References (Справочники) - exactly like Figma
            ReferencesSection(
                onExercisesClick = { navController.navigate(Screen.ExerciseTemplates.route) },
                onProductsClick = { navController.navigate(Screen.FoodItems.route) }
            )
        }
    }
}

@Composable
fun WorkoutTemplateHomeCard(
    name: String,
    onPlay: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                name,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play button (red circle with play icon) - exactly like Figma
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryRed)
                        .clickable { onPlay() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Начать",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Edit text
                Text(
                    "Редактировать",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.clickable { onEdit() }
                )
            }
        }
    }
}

@Composable
fun EmptyTemplateCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "+",
                color = TextSecondary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun CaloriesProgressCard(
    current: Int,
    goal: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left circular progress - consumed calories (red ring)
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { current.toFloat() / goal.toFloat() },
                    modifier = Modifier.size(80.dp),
                    color = PrimaryRed,
                    trackColor = SurfaceVariant,
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round
                )
            }

            // Center text - exactly like Figma
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Сегодня",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                Text(
                    current.toString(),
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Цель",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                Text(
                    goal.toString(),
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Right circular progress - remaining/burned (green ring)
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { 0.6f }, // Placeholder
                    modifier = Modifier.size(80.dp),
                    color = PrimaryGreen,
                    trackColor = SurfaceVariant,
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun ReferencesSection(
    onExercisesClick: () -> Unit,
    onProductsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                "Справочники",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReferenceButton(
                    text = "Упражнения",
                    onClick = onExercisesClick,
                    modifier = Modifier.weight(1f)
                )
                ReferenceButton(
                    text = "Продукты",
                    onClick = onProductsClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ReferenceButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariant)
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
