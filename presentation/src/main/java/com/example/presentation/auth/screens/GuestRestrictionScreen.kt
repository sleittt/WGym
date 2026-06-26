package com.example.presentation.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.presentation.navigation.Screen
import com.example.presentation.ui.components.Button
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary

@Composable
fun GuestRestrictionScreen(
    navController: NavController,
    featureName: String
) {
    Column(
        modifier = Modifier
            .background(Background)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔒",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Доступ ограничен",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$featureName доступно только зарегистрированным пользователям",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            text = "Зарегистрироваться",
            onClick = {
                navController.navigate(Screen.Register.route)
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            text = "Назад",
            onClick = { navController.navigateUp() },
            isPrimary = false
        )
    }
}
