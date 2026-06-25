package com.example.presentation.stats.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.workout.MuscleGroup
import com.example.domain.model.workout.Workout
import com.example.domain.usecase.workout.GetWorkoutHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WorkoutHistoryViewModel @Inject constructor(
    private val getWorkoutHistory: GetWorkoutHistoryUseCase
) : ViewModel() {

    data class WorkoutGroup(
        val date: LocalDate,
        val dateLabel: String,
        val workouts: List<Workout>
    )

    data class UiState(
        val allWorkouts: List<Workout> = emptyList(),
        val filteredWorkouts: List<Workout> = emptyList(),
        val groupedWorkouts: List<WorkoutGroup> = emptyList(),
        val selectedMuscleGroup: MuscleGroup? = null,
        val availableMuscleGroups: List<MuscleGroup> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ru"))

    init {
        loadWorkouts()
    }

    private fun loadWorkouts() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        getWorkoutHistory()
            .onEach { workouts ->
                val muscleGroups = extractMuscleGroups(workouts)
                val grouped = groupByDate(workouts)

                _uiState.update {
                    it.copy(
                        allWorkouts = workouts,
                        filteredWorkouts = workouts,
                        groupedWorkouts = grouped,
                        availableMuscleGroups = muscleGroups,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun setMuscleGroupFilter(muscleGroup: MuscleGroup?) {
        val currentState = _uiState.value
        val filtered = if (muscleGroup == null) {
            currentState.allWorkouts
        } else {
            currentState.allWorkouts.filter { workout ->
                workout.exercises.any { exercise ->
                    exercise.template.muscleGroups.contains(muscleGroup)
                }
            }
        }

        _uiState.update {
            it.copy(
                selectedMuscleGroup = muscleGroup,
                filteredWorkouts = filtered,
                groupedWorkouts = groupByDate(filtered)
            )
        }
    }

    fun getWorkoutsByTemplate(templateId: Int): List<Workout> {
        return _uiState.value.allWorkouts.filter { it.template.id == templateId }
    }

    private fun extractMuscleGroups(workouts: List<Workout>): List<MuscleGroup> {
        return workouts
            .flatMap { workout ->
                workout.exercises.flatMap { it.template.muscleGroups }
            }
            .distinct()
            .sortedBy { it.displayName }
    }

    private fun groupByDate(workouts: List<Workout>): List<WorkoutGroup> {
        return workouts
            .groupBy { it.date }
            .toSortedMap(compareByDescending { it })
            .map { (date, list) ->
                WorkoutGroup(
                    date = date,
                    dateLabel = date.format(dateFormatter),
                    workouts = list
                )
            }
    }

    fun refresh() {
        loadWorkouts()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
