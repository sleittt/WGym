package com.example.presentation.workout.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.workout.Workout
import com.example.domain.usecase.workout.*
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
class WorkoutHistoryViewModel @Inject constructor(
    private val getWorkoutHistory: GetWorkoutHistoryUseCase,
    private val getWorkoutById: GetWorkoutByIdUseCase,
    private val deleteWorkout: DeleteWorkoutUseCase,
    private val getWorkoutCountInPeriod: GetWorkoutCountInPeriodUseCase
) : ViewModel() {

    data class UiState(
        val workouts: List<Workout> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedWorkout: Workout? = null,
        val workoutCountInPeriod: Int = 0,
        val periodStart: LocalDate = LocalDate.now(),
        val periodEnd: LocalDate = LocalDate.now()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadWorkouts()
        loadWorkoutCount()
    }

    private fun loadWorkouts() {
        getWorkoutHistory()
            .onEach { workouts ->
                _uiState.update { it.copy(workouts = workouts, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadWorkoutCount() {
        viewModelScope.launch {
            getWorkoutCountInPeriod(_uiState.value.periodStart, _uiState.value.periodEnd)
                .onSuccess { count ->
                    _uiState.update { it.copy(workoutCountInPeriod = count) }
                }
        }
    }

    fun selectWorkout(workout: Workout?) {
        _uiState.update { it.copy(selectedWorkout = workout) }
    }

    fun deleteWorkoutById(id: String) {
        viewModelScope.launch {
            deleteWorkout(id)
                .onSuccess {
                    _uiState.update { it.copy(selectedWorkout = null) }
                    loadWorkouts()
                    loadWorkoutCount()
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun setPeriod(start: LocalDate, end: LocalDate) {
        _uiState.update { it.copy(periodStart = start, periodEnd = end) }
        loadWorkoutCount()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
