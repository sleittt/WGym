package com.example.presentation.workout.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.manager.WorkoutManager
import com.example.domain.model.workout.Exercise
import com.example.domain.model.workout.WorkoutTemplate
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
    private val getWorkoutTemplateById: GetWorkoutTemplateByIdUseCase,
    private val workoutManager: WorkoutManager
) : ViewModel() {

    val uiState: StateFlow<WorkoutManager.WorkoutState> = workoutManager.workoutState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WorkoutManager.WorkoutState()
        )

    fun loadTemplate(templateId: String) {
        if (workoutManager.hasActiveWorkout()) return

        if (templateId.isBlank() || templateId == "0") {
            workoutManager.startFreeWorkout()
            return
        }

        viewModelScope.launch {
            try {
                val template = getWorkoutTemplateById(templateId).first()
                if (template != null) {
                    workoutManager.startWorkout(template)
                }
            } catch (e: Exception) {
                // handle error
            }
        }
    }

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