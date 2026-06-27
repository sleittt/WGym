package com.example.presentation.stats.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.manager.WorkoutManager // <-- добавлено
import com.example.domain.model.workout.Workout
import com.example.domain.usecase.workout.GetWorkoutHistoryUseCase
import com.example.domain.usecase.workout.GetWorkoutTemplateByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutTemplateHistoryViewModel @Inject constructor(
    private val getWorkoutHistory: GetWorkoutHistoryUseCase,
    private val getWorkoutTemplateById: GetWorkoutTemplateByIdUseCase,
    private val workoutManager: WorkoutManager // <-- добавлено
) : ViewModel() {

    data class UiState(
        val templateName: String = "",
        val workouts: List<Workout> = emptyList(),
        val totalTonnage: Int = 0,
        val avgTonnage: Int = 0,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadTemplateHistory(templateId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val template = getWorkoutTemplateById(templateId).first()
                val templateName = template?.name ?: "Шаблон"

                val repoWorkouts = getWorkoutHistory().first()
                val storedWorkouts = workoutManager.completedWorkouts.value // <-- берём из менеджера
                val allWorkouts = (repoWorkouts + storedWorkouts).distinctBy { it.id } // <-- мержим

                val filtered = allWorkouts.filter { it.template.id == templateId.toIntOrNull() }
                    .sortedByDescending { it.date }

                val totalTonnage = filtered.sumOf { workout ->
                    workout.exercises.sumOf { ex ->
                        ex.sets.sumOf { set -> (set.load * set.reps).toDouble() }
                    }.toInt()
                }

                val avgTonnage = if (filtered.isNotEmpty()) totalTonnage / filtered.size else 0

                _uiState.update {
                    it.copy(
                        templateName = templateName,
                        workouts = filtered,
                        totalTonnage = totalTonnage,
                        avgTonnage = avgTonnage,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки"
                    )
                }
            }
        }
    }
}