package com.example.presentation.stats.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BodyMeasurementsViewModel @Inject constructor() : ViewModel() {

    data class MeasurementTypeConfig(
        val key: String,
        val title: String,
        val unit: String
    )

    data class UiState(
        val config: MeasurementTypeConfig = MeasurementTypeConfig("", "", ""),
        val entries: List<BodyMeasurementsStore.Entry> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val configs = mapOf(
        "weight" to MeasurementTypeConfig("weight", "Вес", "кг"),
        "arm" to MeasurementTypeConfig("arm", "Обхват руки", "см"),
        "waist" to MeasurementTypeConfig("waist", "Обхват талии", "см"),
        "chest" to MeasurementTypeConfig("chest", "Обхват груди", "см"),
        "leg" to MeasurementTypeConfig("leg", "Обхват бедра", "см"),
        "shoulder" to MeasurementTypeConfig("shoulder", "Обхват плеча", "см")
    )

    fun loadType(typeKey: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val config = configs[typeKey] ?: MeasurementTypeConfig(typeKey, typeKey, "")
            val entries = BodyMeasurementsStore.getEntries(typeKey)
            _uiState.update {
                it.copy(
                    config = config,
                    entries = entries,
                    isLoading = false
                )
            }
        }
    }

    fun addEntry(value: Double, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            val type = _uiState.value.config.key
            BodyMeasurementsStore.add(type, value, date)
            loadType(type)
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            val type = _uiState.value.config.key
            BodyMeasurementsStore.delete(type, id)
            loadType(type)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
