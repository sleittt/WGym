package com.example.presentation.stats.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.meal.Meal
import com.example.domain.usecase.meal.GetFoodItemByIdUseCase
import com.example.domain.usecase.meal.GetMealsInPeriodUseCase
import com.example.presentation.ui.components.BarData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NutritionStatsViewModel @Inject constructor(
    private val getMealsInPeriod: GetMealsInPeriodUseCase,
    private val getFoodItemById: GetFoodItemByIdUseCase
) : ViewModel() {

    enum class PeriodMode { WEEK, MONTH }

    data class DayNutrition(
        val date: LocalDate,
        val calories: Int,
        val protein: Double,
        val fat: Double,
        val carbs: Double
    )

    data class WeekNutrition(
        val weekNumber: Int,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val calories: Int,
        val protein: Double,
        val fat: Double,
        val carbs: Double
    )

    data class UiState(
        val mode: PeriodMode = PeriodMode.WEEK,
        val dailyData: List<DayNutrition> = emptyList(),
        val weeklyData: List<WeekNutrition> = emptyList(),
        val barData: List<BarData> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale("ru"))

    init {
        loadWeekData()
    }

    fun setMode(mode: PeriodMode) {
        if (_uiState.value.mode == mode) return
        _uiState.update { it.copy(mode = mode) }
        when (mode) {
            PeriodMode.WEEK -> loadWeekData()
            PeriodMode.MONTH -> loadMonthData()
        }
    }

    private fun loadWeekData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val today = LocalDate.now()
                val weekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)
                    .coerceAtLeast(today.minusDays(6))
                val weekEnd = today

                val meals = getMealsInPeriod(weekStart, weekEnd)
                val dailyData = calculateDailyNutrition(weekStart, weekEnd, meals)
                val barData = dailyData.map {
                    BarData(
                        label = it.date.format(dayFormatter).replaceFirstChar { c -> c.uppercase() },
                        value = it.calories.toFloat(),
                        dateKey = it.date.toString()
                    )
                }

                _uiState.update {
                    it.copy(
                        mode = PeriodMode.WEEK,
                        dailyData = dailyData,
                        barData = barData,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки данных",
                        dailyData = emptyList(),
                        barData = emptyList()
                    )
                }
            }
        }
    }

    private fun loadMonthData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val today = LocalDate.now()
                val monthStart = today.withDayOfMonth(1)
                val monthEnd = today

                val meals = getMealsInPeriod(monthStart, monthEnd)
                val weeklyData = calculateWeeklyNutrition(monthStart, monthEnd, meals)
                val barData = weeklyData.map {
                    BarData(
                        label = "Нед ${it.weekNumber}",
                        value = it.calories.toFloat(),
                        dateKey = it.startDate.toString()
                    )
                }

                _uiState.update {
                    it.copy(
                        mode = PeriodMode.MONTH,
                        weeklyData = weeklyData,
                        barData = barData,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки данных",
                        weeklyData = emptyList(),
                        barData = emptyList()
                    )
                }
            }
        }
    }

    private suspend fun calculateDailyNutrition(
        startDate: LocalDate,
        endDate: LocalDate,
        meals: List<Meal>
    ): List<DayNutrition> {
        val mealsByDate = meals.groupBy { it.date }
        val result = mutableListOf<DayNutrition>()

        var current = startDate
        while (!current.isAfter(endDate)) {
            val dayMeals = mealsByDate[current] ?: emptyList()
            val nutrition = calculateNutrition(dayMeals)
            result.add(
                DayNutrition(
                    date = current,
                    calories = nutrition.calories,
                    protein = nutrition.protein,
                    fat = nutrition.fat,
                    carbs = nutrition.carbs
                )
            )
            current = current.plusDays(1)
        }

        return result
    }

    private suspend fun calculateWeeklyNutrition(
        monthStart: LocalDate,
        monthEnd: LocalDate,
        meals: List<Meal>
    ): List<WeekNutrition> {
        val weekFields = WeekFields.of(Locale.getDefault())
        val mealsByWeek = meals.groupBy {
            it.date.get(weekFields.weekOfWeekBasedYear())
        }

        val currentWeek = monthEnd.get(weekFields.weekOfWeekBasedYear())
        val startWeek = monthStart.get(weekFields.weekOfWeekBasedYear())
        val result = mutableListOf<WeekNutrition>()

        for (week in startWeek..currentWeek) {
            val weekMeals = mealsByWeek[week] ?: emptyList()
            val nutrition = calculateNutrition(weekMeals)

            val weekStart = monthStart.plusWeeks((week - startWeek).toLong())
                .let { if (it.isBefore(monthStart)) monthStart else it }
            val weekEnd = weekStart.plusDays(6).coerceAtMost(monthEnd)

            result.add(
                WeekNutrition(
                    weekNumber = week - startWeek + 1,
                    startDate = weekStart,
                    endDate = weekEnd,
                    calories = nutrition.calories,
                    protein = nutrition.protein,
                    fat = nutrition.fat,
                    carbs = nutrition.carbs
                )
            )
        }

        return result
    }

    private data class NutritionTotals(
        val calories: Int = 0,
        val protein: Double = 0.0,
        val fat: Double = 0.0,
        val carbs: Double = 0.0
    )

    private suspend fun calculateNutrition(meals: List<Meal>): NutritionTotals {
        var calories = 0
        var protein = 0.0
        var fat = 0.0
        var carbs = 0.0

        meals.forEach { meal ->
            meal.items.forEach { item ->
                try {
                    val foodItem = getFoodItemById(item.foodItemId)
                    if (foodItem != null) {
                        val factor = item.amountGrams / 100f
                        calories += (foodItem.caloriesPer100g * factor).toInt()
                        protein += foodItem.proteinPer100g * factor
                        fat += foodItem.fatsPer100g * factor
                        carbs += foodItem.carbsPer100g * factor
                    }
                } catch (e: Exception) {
                    // Пропускаем продукты, которые не удалось загрузить
                }
            }
        }

        return NutritionTotals(calories, protein, fat, carbs)
    }

    fun getSelectedDate(index: Int): LocalDate? {
        return when (_uiState.value.mode) {
            PeriodMode.WEEK -> _uiState.value.dailyData.getOrNull(index)?.date
            PeriodMode.MONTH -> _uiState.value.weeklyData.getOrNull(index)?.startDate
        }
    }

    fun refresh() {
        when (_uiState.value.mode) {
            PeriodMode.WEEK -> loadWeekData()
            PeriodMode.MONTH -> loadMonthData()
        }
    }
}
