package com.example.presentation.auth.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.domain.model.UserRole
import com.example.presentation.auth.AuthViewModel
import com.example.presentation.navigation.Screen

@Composable
fun AuthCheckScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val role by viewModel.userRole.collectAsStateWithLifecycle()

    LaunchedEffect(role) {
        when (role) {
            UserRole.USER -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.AuthCheck.route) { inclusive = true }
                }
            }
            UserRole.GUEST -> {
                navController.navigate(Screen.Register.route) {
                    popUpTo(Screen.AuthCheck.route) { inclusive = true }
                }
            }
            null -> { /* загрузка */ }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
