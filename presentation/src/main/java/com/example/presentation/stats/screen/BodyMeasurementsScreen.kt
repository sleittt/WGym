package com.example.presentation.stats.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.presentation.stats.viewmodels.BodyMeasurementsViewModel
import com.example.presentation.ui.components.BarChart
import com.example.presentation.ui.components.BarData
import com.example.presentation.ui.components.Button
import com.example.presentation.ui.components.Card
import com.example.presentation.ui.components.CircleIconButton
import com.example.presentation.ui.components.LoadingIndicator
import com.example.presentation.ui.components.SectionTitle
import com.example.presentation.ui.components.StatBlock
import com.example.presentation.ui.components.StatRow
import com.example.presentation.ui.components.TextField
import com.example.presentation.ui.components.TopAppBar
import com.example.presentation.ui.theme.Background
import com.example.presentation.ui.theme.PrimaryGreen
import com.example.presentation.ui.theme.PrimaryRed
import com.example.presentation.ui.theme.TextPrimary
import com.example.presentation.ui.theme.TextSecondary
import java.time.format.DateTimeFormatter

@Composable
fun BodyMeasurementsScreen(
    navController: NavController,
    measurementType: String = "weight",
    viewModel: BodyMeasurementsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(measurementType) {
        viewModel.loadType(measurementType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = uiState.config.title,
                navController = navController,
                containerColor = Background
            )
        },
        floatingActionButton = {
            CircleIconButton(
                icon = Icons.Default.Add,
                onClick = { showDialog = true },
                backgroundColor = PrimaryRed
            )
        },
        containerColor = Background
    ) { padding ->
        if (uiState.isLoading) {
            LoadingIndicator(modifier = Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                CurrentValueCard(uiState)

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(title = "Динамика")
                Spacer(modifier = Modifier.height(12.dp))
                ChartSection(uiState)

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(title = "История")
                Spacer(modifier = Modifier.height(12.dp))
                HistorySection(
                    uiState = uiState,
                    onDelete = { viewModel.deleteEntry(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showDialog) {
        AddMeasurementDialog(
            unit = uiState.config.unit,
            onDismiss = { showDialog = false },
            onConfirm = { value ->
                viewModel.addEntry(value)
                showDialog = false
            }
        )
    }
}

@Composable
private fun CurrentValueCard(uiState: BodyMeasurementsViewModel.UiState) {
    val sorted = uiState.entries.sortedByDescending { it.date }
    val current = sorted.firstOrNull()?.value
    val max = uiState.entries.maxOfOrNull { it.value }
    val min = uiState.entries.minOfOrNull { it.value }

    Card(padding = 20.dp, shapeRadius = 16.dp) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Текущий показатель",
                color = TextSecondary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = current?.let { String.format("%.1f", it) } ?: "--",
                    color = TextPrimary,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = uiState.config.unit,
                    color = TextSecondary,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBlock(
                    label = "Максимальный",
                    value = max?.let { String.format("%.1f %s", it, uiState.config.unit) } ?: "--",
                    detail = null
                )
                StatBlock(
                    label = "Минимальный",
                    value = min?.let { String.format("%.1f %s", it, uiState.config.unit) } ?: "--",
                    detail = null
                )
            }
        }
    }
}

@Composable
private fun ChartSection(uiState: BodyMeasurementsViewModel.UiState) {
    val chartEntries = uiState.entries
        .sortedBy { it.date }
        .takeLast(7)

    if (chartEntries.size < 2) {
        Card(padding = 16.dp, shapeRadius = 16.dp) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Недостаточно данных для графика",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }
        return
    }

    val bars = chartEntries.map {
        BarData(
            label = it.date.format(DateTimeFormatter.ofPattern("dd.MM")),
            value = it.value.toFloat(),
            dateKey = it.date.toString()
        )
    }

    Card(padding = 12.dp, shapeRadius = 16.dp) {
        BarChart(
            bars = bars,
            onBarClick = { },
            modifier = Modifier.fillMaxWidth(),
            barColor = PrimaryGreen,
            chartHeight = 180.dp,
            barWidth = 28.dp
        )
    }
}

@Composable
private fun HistorySection(
    uiState: BodyMeasurementsViewModel.UiState,
    onDelete: (Long) -> Unit
) {
    val entries = uiState.entries
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    if (entries.isEmpty()) {
        Card(padding = 16.dp, shapeRadius = 12.dp) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет записей",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
        return
    }

    Card(padding = 0.dp, shapeRadius = 16.dp) {
        Column(modifier = Modifier.fillMaxWidth()) {
            entries.forEachIndexed { index, entry ->
                StatRow(
                    title = String.format("%.1f %s", entry.value, uiState.config.unit),
                    currentValue = entry.date.format(formatter),
                    onClick = { },
                    showDivider = index < entries.size - 1,
                    trailingContent = {
                        IconButton(onClick = { onDelete(entry.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить",
                                tint = PrimaryRed,
                                modifier = Modifier.height(20.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AddMeasurementDialog(
    unit: String,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var valueText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(padding = 20.dp, shapeRadius = 16.dp) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Добавить запись",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = valueText,
                    onValueChange = { valueText = it },
                    label = "Значение ($unit)",
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        text = "Отмена",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        isPrimary = false
                    )
                    Button(
                        text = "Добавить",
                        onClick = {
                            valueText.toDoubleOrNull()?.let { onConfirm(it) }
                        },
                        modifier = Modifier.weight(1f),
                        isPrimary = true
                    )
                }
            }
        }
    }
}
