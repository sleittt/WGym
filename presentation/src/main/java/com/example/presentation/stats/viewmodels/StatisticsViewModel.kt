package com.example.presentation.statistics.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.meal.Meal
import com.example.domain.model.workout.Workout
import com.example.domain.usecase.meal.GetFoodItemByIdUseCase
import com.example.domain.usecase.meal.GetMealsByDateUseCase
import com.example.domain.usecase.meal.GetMealsInPeriodUseCase
import com.example.domain.usecase.workout.GetWorkoutsInPeriodUseCase
import com.example.presentation.stats.viewmodels.BodyMeasurementsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getWorkoutsInPeriod: GetWorkoutsInPeriodUseCase,
    private val getMealsByDate: GetMealsByDateUseCase,
    private val getMealsInPeriod: GetMealsInPeriodUseCase,
    private val getFoodItemById: GetFoodItemByIdUseCase
) : ViewModel() {

    data class WorkoutStats(
        val weekCount: Int = 0,
        val weekTonnage: Int = 0,
        val monthCount: Int = 0,
        val monthTonnage: Int = 0
    )

    data class NutritionStats(
        val todayCalories: Int = 0,
        val todayProtein: Double = 0.0,
        val todayFat: Double = 0.0,
        val todayCarbs: Double = 0.0,
        val weekCalories: Int = 0,
        val weekProtein: Double = 0.0,
        val weekFat: Double = 0.0,
        val weekCarbs: Double = 0.0
    )

    data class MeasurementSummary(
        val current: String = "--",
        val max: String = "--",
        val min: String = "--"
    )

    data class UiState(
        val workoutStats: WorkoutStats = WorkoutStats(),
        val nutritionStats: NutritionStats = NutritionStats(),
        val measurements: Map<String, MeasurementSummary> = emptyMap(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val today = LocalDate.now()
                val weekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)
                    .coerceAtLeast(today.minusDays(6))
                val weekEnd = today
                val monthStart = today.withDayOfMonth(1)
                val monthEnd = today

                val weekWorkoutsDeferred = async {
                    getWorkoutsInPeriod(weekStart, weekEnd).first()
                }
                val monthWorkoutsDeferred = async {
                    getWorkoutsInPeriod(monthStart, monthEnd).first()
                }

                val todayMealsDeferred = async {
                    getMealsByDate(today).first()
                }
                val weekMealsDeferred = async {
                    getMealsInPeriod(weekStart, weekEnd)
                }

                val weekWorkouts = weekWorkoutsDeferred.await()
                val monthWorkouts = monthWorkoutsDeferred.await()
                val todayMeals = todayMealsDeferred.await()
                val weekMeals = weekMealsDeferred.await()

                val weekTonnage = calculateTonnage(weekWorkouts)
                val monthTonnage = calculateTonnage(monthWorkouts)

                val todayNutrition = calculateNutrition(todayMeals)
                val weekNutrition = calculateNutrition(weekMeals)

                val allTypes = BodyMeasurementsStore.data.value.keys
                val measurements = allTypes.associateWith { type ->
                    val entries = BodyMeasurementsStore.getEntries(type)
                    MeasurementSummary(
                        current = entries.firstOrNull()?.value?.toString() ?: "--",
                        max = entries.maxOfOrNull { it.value }?.toString() ?: "--",
                        min = entries.minOfOrNull { it.value }?.toString() ?: "--"
                    )
                }

                _uiState.update {
                    it.copy(
                        workoutStats = WorkoutStats(
                            weekCount = weekWorkouts.size,
                            weekTonnage = weekTonnage,
                            monthCount = monthWorkouts.size,
                            monthTonnage = monthTonnage
                        ),
                        nutritionStats = NutritionStats(
                            todayCalories = todayNutrition.calories,
                            todayProtein = todayNutrition.protein,
                            todayFat = todayNutrition.fat,
                            todayCarbs = todayNutrition.carbs,
                            weekCalories = weekNutrition.calories,
                            weekProtein = weekNutrition.protein,
                            weekFat = weekNutrition.fat,
                            weekCarbs = weekNutrition.carbs
                        ),
                        measurements = measurements,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun calculateTonnage(workouts: List<Workout>): Int {
        return workouts.sumOf { workout ->
            workout.exercises.sumOf { exercise ->
                exercise.sets.sumOf { set ->
                    (set.load * set.reps).toDouble()
                }.toInt()
            }
        }
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
                val foodItem = getFoodItemById(item.foodItemId)
                if (foodItem != null) {
                    val factor = item.amountGrams / 100f
                    calories += (foodItem.caloriesPer100g * factor).toInt()
                    protein += foodItem.proteinPer100g * factor
                    fat += foodItem.fatsPer100g * factor
                    carbs += foodItem.carbsPer100g * factor
                }
            }
        }

        return NutritionTotals(calories, protein, fat, carbs)
    }

    fun refresh() {
        loadStatistics()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
