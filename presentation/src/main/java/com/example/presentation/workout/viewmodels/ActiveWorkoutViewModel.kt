package com.example.presentation.workout.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.manager.WorkoutManager
import com.example.domain.model.workout.Exercise
import com.example.domain.model.workout.Set
import com.example.domain.model.workout.SetType
import com.example.domain.model.workout.WorkoutTemplate
import com.example.domain.usecase.workout.GetExerciseTemplateByIdUseCase
import com.example.domain.usecase.workout.GetWorkoutTemplateByIdUseCase
import com.example.domain.usecase.workout.UpdateWorkoutTemplateUseCase // <-- добавлено
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val workoutManager: WorkoutManager,
    private val savedStateHandle: SavedStateHandle,
    private val getWorkoutTemplateByIdUseCase: GetWorkoutTemplateByIdUseCase,
    private val getExerciseTemplateById: GetExerciseTemplateByIdUseCase,
    private val updateWorkoutTemplate: UpdateWorkoutTemplateUseCase // <-- добавлено
) : ViewModel() {

    init {
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

    fun fetchAndAddExercise(exerciseTemplateId: String) {
        viewModelScope.launch {
            getExerciseTemplateById(exerciseTemplateId)?.let { template ->
                val exercise = Exercise(
                    id = workoutManager.workoutState.value.exercises.size,
                    template = template,
                    note = "",
                    sets = listOf(
                        Set(id = 0, rest = 90.seconds, load = 0f, reps = 10, type = SetType.NORMAL)
                    ),
                    order = workoutManager.workoutState.value.exercises.size
                )
                workoutManager.addExercise(exercise)
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
        // <-- Сохраняем обновлённый шаблон в репозиторий
        saveTemplateChanges()
    }

    private fun saveTemplateChanges() {
        val template = workoutManager.currentTemplate ?: return
        if (template.id == 0) return // не сохраняем свободную тренировку

        viewModelScope.launch {
            val updatedExercises = workoutManager.workoutState.value.exercises.map { ews ->
                ews.exercise.copy(
                    sets = ews.sets.mapIndexed { index, setData ->
                        Set(
                            id = index,
                            rest = setData.restDuration,
                            load = setData.weight.toFloatOrNull() ?: 0f,
                            reps = setData.reps.toIntOrNull() ?: 0,
                            type = setData.type,
                            isCompleted = setData.isCompleted
                        )
                    }
                )
            }
            val updatedTemplate = template.copy(
                exercise = updatedExercises,
                useCount = template.useCount + 1
            )
            updateWorkoutTemplate(template.id.toString(), updatedTemplate)
        }
    }

    fun dismissFinishDialog() {
        workoutManager.dismissFinishDialog()
    }

    fun markAllSetsCompletedAndFinish() {
        workoutManager.markAllSetsCompletedAndFinish()
        saveTemplateChanges() // <-- сохраняем и здесь
    }

    fun removeUncompletedSetsAndFinish() {
        workoutManager.removeUncompletedSetsAndFinish()
        saveTemplateChanges() // <-- и здесь
    }
}