package com.example.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.presentation.ui.theme.*

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Triple(Screen.Home, "Главная", Icons.Default.Home),
        Triple(Screen.WorkoutTemplates, "Тренировки", Icons.Default.Call),
        Triple(Screen.Nutrition, "Питание", Icons.Default.AddCircle),
        Triple(Screen.Statistics, "Статистика", Icons.Default.Create),
        Triple(Screen.Settings, "Настройки", Icons.Default.Settings)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = items.any { it.first.route == currentRoute }

    if (showBottomBar) {
        NavigationBar(containerColor = Background, contentColor = TextPrimary) {
            items.forEach { (screen, label, icon) ->
                NavigationBarItem(
                    icon = { Icon(icon, contentDescription = label, tint = if (currentRoute == screen.route) PrimaryRed else TextSecondary) },
                    label = { Text(label, color = if (currentRoute == screen.route) PrimaryRed else TextSecondary) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryRed,
                        selectedTextColor = PrimaryRed,
                        indicatorColor = SurfaceVariant
                    )
                )
            }
        }
    }
}
