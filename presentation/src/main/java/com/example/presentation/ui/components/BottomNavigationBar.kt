package com.example.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.presentation.navigation.Screen
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.Surface
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
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (currentRoute != null && currentRoute !in bottomNavRoutes) return

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
                            popUpTo(0) { inclusive = true }  // выкидываем ВСЁ
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