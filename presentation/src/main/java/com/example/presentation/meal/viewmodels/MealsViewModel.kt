package com.example.presentation.meal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.meal.Meal
import com.example.domain.model.meal.MealType
import com.example.domain.usecase.meal.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MealsViewModel @Inject constructor(
    private val getMealsByDate: GetMealsByDateUseCase,
    private val addFoodToMeal: AddFoodToMealUseCase,
    private val removeFoodFromMeal: RemoveFoodFromMealUseCase
) : ViewModel() {

    data class UiState(
        val selectedDate: LocalDate = LocalDate.now(),
        val meals: List<Meal> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedMealType: MealType? = null,
        val isAddFoodDialogOpen: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadMeals()
    }

    private fun loadMeals() {
        getMealsByDate(_uiState.value.selectedDate)
            .onEach { meals ->
                _uiState.update { it.copy(meals = meals, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun setDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        loadMeals()
    }

    fun openAddFoodDialog(mealType: MealType) {
        _uiState.update { it.copy(selectedMealType = mealType, isAddFoodDialogOpen = true) }
    }

    fun dismissAddFoodDialog() {
        _uiState.update { it.copy(isAddFoodDialogOpen = false, selectedMealType = null) }
    }

    fun addFoodToMeal(foodItemId: String, amountGrams: Float) {
        val mealType = _uiState.value.selectedMealType ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            addFoodToMeal(_uiState.value.selectedDate, mealType, foodItemId, amountGrams)
                .onSuccess {
                    dismissAddFoodDialog()
                    loadMeals()
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun removeFood(mealId: String, foodItemId: String) {
        viewModelScope.launch {
            removeFoodFromMeal(mealId, foodItemId)
                .onSuccess { loadMeals() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
