package com.example.presentation.meal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.meal.MealType
import com.example.domain.usecase.meal.AddFoodToMealUseCase
import com.example.domain.usecase.meal.GetFoodItemByIdUseCase
import com.example.domain.usecase.meal.RemoveFoodFromMealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MealItemDetailViewModel @Inject constructor(
    private val getFoodItemByIdUseCase: GetFoodItemByIdUseCase,
    private val addFoodToMealUseCase: AddFoodToMealUseCase,
    private val removeFoodFromMealUseCase: RemoveFoodFromMealUseCase
) : ViewModel() {

    data class UiState(
        val foodItemName: String = "",
        val foodItemId: String = "",
        val mealItemId: String? = null,
        val amountGrams: String = "100",
        val selectedMealType: MealType = MealType.BREAKFAST,
        val date: LocalDate = LocalDate.now(),
        val isEditing: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    ) {
        val isValid: Boolean
            get() = amountGrams.toFloatOrNull() != null && amountGrams.toFloatOrNull()!! > 0
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(
        foodItemId: String,
        mealItemId: String? = null,
        initialMealType: String? = null,
        initialAmount: Float? = null,
        dateString: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val foodItem = getFoodItemByIdUseCase(foodItemId)
            val mealType = initialMealType?.let {
                try {
                    MealType.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    MealType.BREAKFAST
                }
            } ?: MealType.BREAKFAST

            // Парсим дату из строки, иначе используем сегодня
            val date = try {
                dateString?.let { LocalDate.parse(it) } ?: LocalDate.now()
            } catch (e: Exception) {
                LocalDate.now()
            }

            _uiState.update {
                it.copy(
                    foodItemId = foodItemId,
                    foodItemName = foodItem?.name ?: "Продукт",
                    mealItemId = mealItemId,
                    amountGrams = initialAmount?.toString() ?: "100",
                    selectedMealType = mealType,
                    date = date,
                    isEditing = mealItemId != null,
                    isLoading = false
                )
            }
        }
    }

    fun onAmountChange(value: String) {
        _uiState.update { it.copy(amountGrams = value) }
    }

    fun onMealTypeChange(mealType: MealType) {
        _uiState.update { it.copy(selectedMealType = mealType) }
    }

    fun onSave(onSuccess: () -> Unit) {
        val state = uiState.value
        if (!state.isValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val result = addFoodToMealUseCase(
                    date = state.date,  // ← используем дату из состояния (может быть любая)
                    mealType = state.selectedMealType,
                    foodItemId = state.foodItemId,
                    amountGrams = state.amountGrams.toFloatOrNull() ?: 100f
                )

                result.fold(
                    onSuccess = {
                        onSuccess()
                    },
                    onFailure = { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onDelete(onSuccess: () -> Unit) {
        val state = uiState.value
        val mealId = state.mealItemId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val result = removeFoodFromMealUseCase(
                    mealId = mealId,
                    foodItemId = state.foodItemId
                )

                result.fold(
                    onSuccess = { onSuccess() },
                    onFailure = { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
