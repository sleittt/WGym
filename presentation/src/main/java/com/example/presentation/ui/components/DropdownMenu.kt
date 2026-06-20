package com.example.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.Surface
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary

data class DropdownAction(
    val text: String,
    val isDanger: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun DropdownMenu(
    actions: List<DropdownAction>,
    modifier: Modifier = Modifier,
    iconSize: Dp = 20.dp
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(
        onClick = { expanded = true },
        modifier = modifier
    ) {
        Icon(
            Icons.Default.MoreVert,
            contentDescription = "Меню",
            tint = TextSecondary,
            modifier = Modifier.size(iconSize)
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.background(Surface)
    ) {
        actions.forEach { action ->
            DropdownMenuItem(
                text = {
                    Text(
                        action.text,
                        color = if (action.isDanger) PrimaryRed else TextPrimary
                    )
                },
                onClick = {
                    expanded = false
                    action.onClick()
                }
            )
        }
    }
}