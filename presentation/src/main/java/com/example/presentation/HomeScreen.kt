package com.example.presentation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.domain.manager.WorkoutManager
import com.example.domain.model.UserRole
import com.example.presentation.auth.AuthViewModel
import com.example.presentation.meal.viewmodels.NutritionViewModel
import com.example.presentation.navigation.Screen
import com.example.presentation.ui.components.BottomNavigationBar
import com.example.presentation.ui.components.Card
import com.example.presentation.ui.components.CircularProgress
import com.example.presentation.ui.components.EmptyCard
import com.example.presentation.ui.components.PlayIconButton
import com.example.presentation.ui.components.SectionTitle
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryGreen
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.SurfaceVariant
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import com.example.presentation.workout.viewmodels.WorkoutManagerViewModel
import com.example.presentation.workout.viewmodels.WorkoutTemplatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: WorkoutTemplatesViewModel = hiltViewModel(),
    workoutManagerVm: WorkoutManagerViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    nutritionViewModel: NutritionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val workoutManager = workoutManagerVm.workoutManager
    val workoutState by workoutManager.workoutState.collectAsState()
    val hasActiveWorkout = workoutManager.hasActiveWorkout()
    val userRole by authViewModel.userRole.collectAsStateWithLifecycle()

    // ← РЕАЛЬНЫЕ ДАННЫЕ ПИТАНИЯ
    val nutritionUiState by nutritionViewModel.uiState.collectAsStateWithLifecycle()

    // Обновляем данные питания при каждом входе на экран
    DisposableEffect(Unit) {
        nutritionViewModel.refresh()
        onDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "WGym",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (hasActiveWorkout) {
                            Spacer(modifier = Modifier.width(12.dp))
                            WorkoutTimerInTopBar(
                                workoutState = workoutState,
                                onClick = {
                                    navController.navigate(Screen.ActiveWorkout.createRoute("0"))
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController, workoutManager) },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // === БАННЕР ДЛЯ ГОСТЯ ===
            if (userRole == UserRole.GUEST) {
                GuestBanner(
                    onRegisterClick = {
                        navController.navigate(Screen.Register.route)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            SectionTitle(
                title = "Шаблоны тренировок",
                actionText = "Все",
                onActionClick = { navController.navigate(Screen.WorkoutTemplates.route) }
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
                        onPlay = {
                            if (hasActiveWorkout) {
                                navController.navigate(Screen.ActiveWorkout.createRoute("0"))
                            } else {
                                navController.navigate(Screen.ActiveWorkout.createRoute(template.id.toString()))
                            }
                        },
                        onEdit = {
                            navController.navigate(Screen.WorkoutTemplates.route)
                        }
                    )
                }
                if (uiState.templates.size < 4) {
                    items(4 - uiState.templates.size) {
                        EmptyCard(
                            onClick = { navController.navigate(Screen.WorkoutTemplates.route) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Calories Progress — реальные данные
            if (userRole == UserRole.USER) {
                CaloriesProgressCard(
                    current = nutritionUiState.currentCalories,
                    goal = nutritionUiState.goalCalories,
                    currentMacros = nutritionUiState.currentMacros
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // References — только для пользователей
            if (userRole == UserRole.USER) {
                ReferencesSection(
                    onExercisesClick = { navController.navigate(Screen.ExerciseTemplates.createRoute(selectMode = false)) },
                    onProductsClick = { navController.navigate(Screen.FoodItems.route) }
                )
            }
        }
    }
}

@Composable
fun GuestBanner(
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PrimaryRed.copy(alpha = 0.15f))
            .clickable { onRegisterClick() }
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "👋 Вы используете приложение как гость",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Зарегистрируйтесь, чтобы получить доступ к питанию, статистике и справочникам",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Нажмите, чтобы зарегистрироваться →",
                color = PrimaryRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun WorkoutTimerInTopBar(
    workoutState: WorkoutManager.WorkoutState,
    onClick: () -> Unit
) {
    val activeRestTimer = workoutState.activeRestTimer
    val isRestTimerRunning = activeRestTimer != null && activeRestTimer.isRunning

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryGreen.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isRestTimerRunning) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    activeRestTimer!!.formattedTime,
                    color = PrimaryGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                LinearProgressIndicator(
                    progress = { activeRestTimer.progress.progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .width(50.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = PrimaryGreen,
                    trackColor = PrimaryGreen.copy(alpha = 0.2f)
                )
            }
        } else {
            Text(
                workoutState.elapsedTimeFormatted,
                color = PrimaryGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
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
        modifier = Modifier.aspectRatio(1.2f),
        padding = 12.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
                PlayIconButton(
                    onClick = onPlay,
                    size = 32.dp
                )

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
fun CaloriesProgressCard(
    current: Int,
    goal: Int,
    currentMacros: com.example.presentation.ui.components.MacroValues? = null
) {
    Card(
        shapeRadius = 20.dp,
        padding = 20.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgress(
                    current = current,
                    goal = goal,
                    progressColor = PrimaryRed,
                    size = 80.dp,
                    strokeWidth = 6.dp
                )

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

                val remaining = (goal - current).coerceAtLeast(0)
                CircularProgress(
                    current = remaining,
                    goal = goal,
                    progressColor = PrimaryGreen,
                    size = 80.dp,
                    strokeWidth = 6.dp
                )
            }

            // ← БЖУ под калориями, если данные есть
            if (currentMacros != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MacroLabel("Белки", currentMacros.protein.toInt(), 210, PrimaryRed)
                    MacroLabel("Жиры", currentMacros.fat.toInt(), 80, PrimaryGreen)
                    MacroLabel("Углев", currentMacros.carbs.toInt(), 210, TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun MacroLabel(
    label: String,
    current: Int,
    goal: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp
        )
        Text(
            text = "$current / $goal",
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ReferencesSection(
    onExercisesClick: () -> Unit,
    onProductsClick: () -> Unit
) {
    Card(
        shapeRadius = 20.dp,
        padding = 20.dp
    ) {
        Column {
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
    androidx.compose.foundation.layout.Box(
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