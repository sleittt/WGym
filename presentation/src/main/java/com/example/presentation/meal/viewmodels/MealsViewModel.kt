package com.example.presentation.meal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.meal.FoodItem
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

data class NutritionSummary(
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val fats: Double = 0.0,
    val carbs: Double = 0.0
) {
    operator fun plus(other: NutritionSummary): NutritionSummary =
        NutritionSummary(
            calories = this.calories + other.calories,
            protein = this.protein + other.protein,
            fats = this.fats + other.fats,
            carbs = this.carbs + other.carbs
        )
}

@HiltViewModel
class MealsViewModel @Inject constructor(
    private val getMealsByDate: GetMealsByDateUseCase,
    private val getFoodItemById: GetFoodItemByIdUseCase,
    private val addFoodToMeal: AddFoodToMealUseCase,
    private val removeFoodFromMeal: RemoveFoodFromMealUseCase
) : ViewModel() {

    data class UiState(
        val selectedDate: LocalDate = LocalDate.now(),
        val meals: List<Meal> = emptyList(),
        val foodItems: Map<String, FoodItem> = emptyMap(), // Cache for lookups
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedMealType: MealType? = null,
        val isAddFoodDialogOpen: Boolean = false,
        // Nutrition goals (could come from User settings)
        val calorieGoal: Int = 2000,
        val proteinGoal: Float = 150f,
        val fatGoal: Float = 70f,
        val carbsGoal: Float = 250f
    ) {
        // Derived state: real-time nutrition calculation
        val dailySummary: NutritionSummary
            get() = meals.fold(NutritionSummary()) { acc, meal ->
                acc + meal.items.fold(NutritionSummary()) { itemAcc, item ->
                    val food = foodItems[item.foodItemId]
                    if (food != null) {
                        val ratio = item.amountGrams / 100f
                        itemAcc + NutritionSummary(
                            calories = food.caloriesPer100g * ratio,
                            protein = food.proteinPer100g * ratio,
                            fats = food.fatsPer100g * ratio,
                            carbs = food.carbsPer100g * ratio
                        )
                    } else itemAcc
                }
            }

        val caloriesRemaining: Int
            get() = (calorieGoal - dailySummary.calories).toInt().coerceAtLeast(0)

        val isCalorieGoalReached: Boolean
            get() = dailySummary.calories >= calorieGoal
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadMeals()
    }

    private fun loadMeals() {
        getMealsByDate(_uiState.value.selectedDate)
            .onEach { meals ->
                // Load food items for all meal items
                val foodIds = meals.flatMap { it.items }.map { it.foodItemId }.toSet()
                val foodMap = mutableMapOf<String, FoodItem>()
                foodIds.forEach { id ->
                    getFoodItemById(id)?.let { foodMap[id] = it }
                }
                _uiState.update { it.copy(
                    meals = meals,
                    foodItems = foodMap,
                    isLoading = false
                ) }
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

    fun setGoals(calorieGoal: Int, proteinGoal: Float, fatGoal: Float, carbsGoal: Float) {
        _uiState.update {
            it.copy(
                calorieGoal = calorieGoal,
                proteinGoal = proteinGoal,
                fatGoal = fatGoal,
                carbsGoal = carbsGoal
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
