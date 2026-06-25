package com.example.presentation.workout.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.manager.WorkoutManager
import com.example.domain.model.workout.Exercise
import com.example.domain.model.workout.SetType
import com.example.domain.usecase.workout.GetWorkoutTemplateByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val workoutManager: WorkoutManager,
    private val savedStateHandle: SavedStateHandle,
    private val getWorkoutTemplateByIdUseCase: GetWorkoutTemplateByIdUseCase
) : ViewModel() {

    init {
        // Запускаем тренировку только если нет активной (Singleton сохраняет состояние)
        if (!workoutManager.hasActiveWorkout()) {
            val templateId = savedStateHandle.get<String>("templateId") ?: "0"
            viewModelScope.launch {
                if (templateId != "0" && templateId.isNotBlank()) {
                    getWorkoutTemplateByIdUseCase(templateId).first()?.let { template ->
                        workoutManager.startWorkout(template)
                    } ?: workoutManager.startFreeWorkout()
                } else {
                    workoutManager.startFreeWorkout()
                }
            }
        }
    }

    val uiState: StateFlow<WorkoutManager.WorkoutState> = workoutManager.workoutState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WorkoutManager.WorkoutState()
        )

    fun toggleSetCompletion(exerciseIndex: Int, setIndex: Int) {
        workoutManager.toggleSetCompletion(exerciseIndex, setIndex)
    }

    fun skipRestTimer(exerciseIndex: Int, setIndex: Int) {
        workoutManager.skipRestTimer(exerciseIndex, setIndex)
    }

    fun adjustRestTime(exerciseIndex: Int, setIndex: Int, deltaSeconds: Int) {
        workoutManager.adjustRestTime(exerciseIndex, setIndex, deltaSeconds)
    }

    fun updateSetWeight(exerciseIndex: Int, setIndex: Int, weight: String) {
        workoutManager.updateSetWeight(exerciseIndex, setIndex, weight)
    }

    fun updateSetReps(exerciseIndex: Int, setIndex: Int, reps: String) {
        workoutManager.updateSetReps(exerciseIndex, setIndex, reps)
    }

    fun addExercise(exercise: Exercise) {
        workoutManager.addExercise(exercise)
    }

    fun addSet(exerciseIndex: Int) {
        workoutManager.addSet(exerciseIndex)
    }

    fun removeSet(exerciseIndex: Int, setIndex: Int) {
        workoutManager.removeSet(exerciseIndex, setIndex)
    }

    fun changeSetType(exerciseIndex: Int, setIndex: Int, type: SetType) {
        workoutManager.changeSetType(exerciseIndex, setIndex, type)
    }

    fun startRestTimer(exerciseIndex: Int, setIndex: Int) {
        workoutManager.startRestTimer(exerciseIndex, setIndex)
    }

    fun finishWorkout() {
        workoutManager.finishWorkout()
    }

    fun dismissFinishDialog() {
        workoutManager.dismissFinishDialog()
    }

    fun markAllSetsCompletedAndFinish() {
        workoutManager.markAllSetsCompletedAndFinish()
    }

    fun removeUncompletedSetsAndFinish() {
        workoutManager.removeUncompletedSetsAndFinish()
    }
}
