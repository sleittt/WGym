package com.example.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.domain.manager.WorkoutManager
import com.example.presentation.navigation.Screen
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryGreen
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.Surface
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    data object Home : BottomNavItem(Screen.Home.route, Icons.Default.Home, "Главная")
    data object Workouts : BottomNavItem(Screen.WorkoutTemplates.route, Icons.Default.Create, "Тренировки")
    data object Nutrition : BottomNavItem(Screen.Nutrition.route, Icons.Default.Info, "Питание")
    data object Statistics : BottomNavItem(Screen.Statistics.route, Icons.Default.Person, "Статистика")
    data object Profile : BottomNavItem(Screen.Settings.route, Icons.Default.Person, "Профиль")
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Workouts,
    BottomNavItem.Nutrition,
    BottomNavItem.Statistics,
    BottomNavItem.Profile
)

val bottomNavRoutes = setOf(
    Screen.Home.route,
    Screen.WorkoutTemplates.route,
    Screen.Nutrition.route,
    Screen.Statistics.route,
    Screen.Settings.route
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    workoutManager: WorkoutManager
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (currentRoute != null && currentRoute !in bottomNavRoutes) return

    val workoutState by workoutManager.workoutState.collectAsState()
    val hasActiveWorkout = workoutManager.hasActiveWorkout()
    val activeRestTimer = workoutState.activeRestTimer
    val isRestTimerRunning = activeRestTimer != null && activeRestTimer.isRunning

    Column {
        // === СТРОЧКА АКТИВНОЙ ТРЕНИРОВКИ ===
        if (hasActiveWorkout) {
            ActiveWorkoutBar(
                workoutState = workoutState,
                isRestTimerRunning = isRestTimerRunning,
                activeRestTimer = activeRestTimer,
                onClick = {
                    navController.navigate(Screen.ActiveWorkout.createRoute("0")) {
                        launchSingleTop = true
                    }
                }
            )
        }

        NavigationBar(
            containerColor = Surface,
            tonalElevation = 0.dp
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (selected) PrimaryRed else TextSecondary
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            color = if (selected) PrimaryRed else TextSecondary
                        )
                    },
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(item.route) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryRed,
                        selectedTextColor = PrimaryRed,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = Background
                    )
                )
            }
        }
    }
}

@Composable
private fun ActiveWorkoutBar(
    workoutState: WorkoutManager.WorkoutState,
    isRestTimerRunning: Boolean,
    activeRestTimer: WorkoutManager.ActiveRestTimer?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryGreen.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Название тренировки
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workoutState.templateName.takeIf { it.isNotBlank() } ?: "Активная тренировка",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Text(
                    text = "Нажмите, чтобы продолжить",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            // Таймер
            if (isRestTimerRunning && activeRestTimer != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = activeRestTimer.formattedTime,
                        color = PrimaryGreen,
                        fontSize = 14.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = { activeRestTimer.progress.progress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .width(50.dp)
                            .height(3.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(2.dp)),
                        color = PrimaryGreen,
                        trackColor = PrimaryGreen.copy(alpha = 0.2f)
                    )
                }
            } else {
                Text(
                    text = workoutState.elapsedTimeFormatted,
                    color = PrimaryGreen,
                    fontSize = 16.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}