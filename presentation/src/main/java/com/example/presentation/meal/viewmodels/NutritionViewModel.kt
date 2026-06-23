package com.example.presentation.meal.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.meal.Meal
import com.example.domain.model.meal.MealType
import com.example.domain.usecase.meal.GetFoodItemByIdUseCase
import com.example.domain.usecase.meal.GetMealsByDateUseCase
import com.example.presentation.ui.components.MacroValues
import com.example.presentation.ui.components.MealItemUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMealsByDateUseCase: GetMealsByDateUseCase,
    private val getFoodItemByIdUseCase: GetFoodItemByIdUseCase
) : ViewModel() {

    data class MealSectionUi(
        val mealId: String,
        val type: MealType,
        val title: String,
        val items: List<MealItemUi>
    )

    data class UiState(
        val currentDate: String = "",
        val currentCalories: Int = 0,
        val goalCalories: Int = 2000,
        val currentMacros: MacroValues = MacroValues(0.0, 0.0, 0.0),
        val goalMacros: MacroValues = MacroValues(210.0, 210.0, 80.0),
        val meals: List<MealSectionUi> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    companion object {
        private const val KEY_SELECTED_DATE = "nutrition_selected_date"
    }

    private val _selectedDate = MutableStateFlow(
        savedStateHandle.get<String>(KEY_SELECTED_DATE)?.let {
            try { LocalDate.parse(it) } catch(e: Exception) { null }
        } ?: LocalDate.now()
    )
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getMealsByDateUseCase(_selectedDate.value)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Ошибка загрузки данных") }
                }
                .onEach { meals ->
                    val newState = buildUiState(meals)
                    _uiState.update { newState }
                }
                .launchIn(viewModelScope)
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        savedStateHandle[KEY_SELECTED_DATE] = date.toString()
        loadData()
    }

    fun refresh() {
        loadData()
    }

    private suspend fun buildUiState(meals: List<Meal>): UiState {
        var totalCalories = 0
        var totalProtein = 0.0
        var totalFat = 0.0
        var totalCarbs = 0.0

        val mealSections = MealType.entries.map { mealType ->
            val meal = meals.find { it.type == mealType }
            val items = meal?.items?.mapNotNull { item ->
                val foodItem = getFoodItemByIdUseCase(item.foodItemId)
                if (foodItem == null) return@mapNotNull null

                val amountFactor = item.amountGrams / 100f
                val calories = ((foodItem.caloriesPer100g) * amountFactor).toInt()
                val protein = (foodItem.proteinPer100g) * amountFactor
                val fat = (foodItem.fatsPer100g) * amountFactor
                val carbs = (foodItem.carbsPer100g) * amountFactor

                totalCalories += calories
                totalProtein += protein
                totalFat += fat
                totalCarbs += carbs

                MealItemUi(
                    id = "${meal.id}_${item.foodItemId}",
                    foodItemId = item.foodItemId,
                    name = foodItem.name,
                    amountGrams = item.amountGrams,
                    calories = calories,
                    protein = protein,
                    fat = fat,
                    carbs = carbs
                )
            } ?: emptyList()

            MealSectionUi(
                mealId = meal?.id ?: "",
                type = mealType,
                title = getMealTitle(mealType),
                items = items
            )
        }

        val formatter = DateTimeFormatter.ofPattern("dd MMMM", Locale("ru"))

        return UiState(
            currentDate = _selectedDate.value.format(formatter),
            currentCalories = totalCalories,
            goalCalories = 2000,
            currentMacros = MacroValues(carbs = totalCarbs, protein = totalProtein, fat = totalFat),
            goalMacros = MacroValues(carbs = 210.0, protein = 210.0, fat = 80.0),
            meals = mealSections,
            isLoading = false,
            error = null
        )
    }

    private fun getMealTitle(type: MealType): String = when (type) {
        MealType.BREAKFAST -> "Утро"
        MealType.LUNCH -> "День"
        MealType.DINNER -> "Вечер"
        MealType.SNACK -> "Перекус"
    }
}
