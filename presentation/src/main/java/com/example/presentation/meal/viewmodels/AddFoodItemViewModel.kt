package com.example.presentation.meal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.meal.FoodItem
import com.example.domain.usecase.meal.AddFoodItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddFoodItemViewModel @Inject constructor(
    private val addFoodItemsUseCase: AddFoodItemsUseCase
) : ViewModel() {

    data class UiState(
        val name: String = "",
        val calories: String = "",
        val protein: String = "",
        val fat: String = "",
        val carbs: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) {
        val isValid: Boolean
            get() = name.isNotBlank() &&
                    calories.toDoubleOrNull() != null && calories.toDoubleOrNull()!! > 0 &&
                    protein.toDoubleOrNull() != null && protein.toDoubleOrNull()!! >= 0 &&
                    fat.toDoubleOrNull() != null && fat.toDoubleOrNull()!! >= 0 &&
                    carbs.toDoubleOrNull() != null && carbs.toDoubleOrNull()!! >= 0
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onCaloriesChange(value: String) {
        _uiState.update { it.copy(calories = value) }
    }

    fun onProteinChange(value: String) {
        _uiState.update { it.copy(protein = value) }
    }

    fun onFatChange(value: String) {
        _uiState.update { it.copy(fat = value) }
    }

    fun onCarbsChange(value: String) {
        _uiState.update { it.copy(carbs = value) }
    }

    fun onAddClick(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = uiState.value
        if (!state.isValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val foodItem = FoodItem(
                id = UUID.randomUUID().toString(),
                name = state.name.trim(),
                caloriesPer100g = state.calories.toDouble(),
                proteinPer100g = state.protein.toDouble(),
                fatsPer100g = state.fat.toDouble(),
                carbsPer100g = state.carbs.toDouble()
            )

            addFoodItemsUseCase(foodItem)
                .onSuccess {
                    _uiState.update { UiState() } // reset
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                    onError(e.message ?: "Ошибка")
                }
        }
    }
}
