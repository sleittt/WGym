package com.example.presentation.workout.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.workout.Exercise
import com.example.domain.model.workout.Set
import com.example.domain.model.workout.SetType
import com.example.domain.model.workout.Workout
import com.example.domain.model.workout.WorkoutTemplate
import com.example.domain.usecase.workout.CreateWorkoutUseCase
import com.example.domain.usecase.workout.GetWorkoutTemplateByIdUseCase
import com.example.domain.usecase.workout.UpdateWorkoutTemplateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val getWorkoutTemplateById: GetWorkoutTemplateByIdUseCase,
    private val createWorkout: CreateWorkoutUseCase,
    private val updateWorkoutTemplate: UpdateWorkoutTemplateUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class ExerciseWithSets(
        val exercise: Exercise,
        val sets: List<SetUiState> = emptyList()
    )

    data class SetUiState(
        val setNumber: String,
        val previous: String,
        val weight: String,
        val reps: String,
        val isCompleted: Boolean = false,
        val restTime: Duration? = null,
        val type: SetType = SetType.NORMAL
    )

    data class UiState(
        val templateName: String = "",
        val exercises: List<ExerciseWithSets> = emptyList(),
        val isRunning: Boolean = false,
        val elapsedSeconds: Long = 0,
        val isLoading: Boolean = false,
        val error: String? = null,
        val canFinish: Boolean = false,
        val isFinished: Boolean = false,
        val workoutId: String? = null
    ) {
        val elapsedTimeFormatted: String
            get() {
                val hours = elapsedSeconds / 3600
                val minutes = (elapsedSeconds % 3600) / 60
                val seconds = elapsedSeconds % 60
                return if (hours > 0) {
                    String.format("%d:%02d:%02d", hours, minutes, seconds)
                } else {
                    String.format("%02d:%02d", minutes, seconds)
                }
            }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: kotlin.time.Instant? = null
    private var endTime: kotlin.time.Instant? = null
    private var currentTemplate: WorkoutTemplate? = null

    fun loadTemplate(templateId: String) {
        if (templateId.isBlank() || templateId == "0") {
            _uiState.update { it.copy(templateName = "Свободная тренировка", isRunning = true) }
            startTimer()
            startTime = kotlin.time.Clock.System.now()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val template = getWorkoutTemplateById(templateId).first()
                currentTemplate = template
                if (template != null) {
                    startWorkout(template)
                } else {
                    _uiState.update { it.copy(error = "Шаблон не найден", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun startWorkout(template: WorkoutTemplate) {
        startTime = kotlin.time.Clock.System.now()
        _uiState.update {
            it.copy(
                templateName = template.name,
                exercises = template.exercise.map { ex ->
                    ExerciseWithSets(
                        exercise = ex,
                        sets = ex.sets.mapIndexed { index, set ->
                            SetUiState(
                                setNumber = when {
                                    set.type == SetType.WARMUP -> "W"
                                    index == ex.sets.lastIndex && set.type == SetType.FAILURE -> "F"
                                    else -> (index + 1).toString()
                                },
                                previous = "${set.load.toInt()} кг x ${set.reps}",
                                weight = set.load.toString(),
                                reps = set.reps.toString(),
                                type = set.type
                            )
                        }
                    )
                },
                isRunning = true,
                elapsedSeconds = 0,
                isLoading = false
            )
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    fun toggleSetCompletion(exerciseIndex: Int, setIndex: Int) {
        _uiState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val sets = exercise.sets.toMutableList()
            val set = sets[setIndex]

            sets[setIndex] = set.copy(
                isCompleted = !set.isCompleted,
                restTime = if (!set.isCompleted) 90.seconds else null
            )
            exercises[exerciseIndex] = exercise.copy(sets = sets)
            state.copy(
                exercises = exercises,
                canFinish = exercises.all { ex -> ex.sets.any { it.isCompleted } }
            )
        }
    }

    fun updateSetWeight(exerciseIndex: Int, setIndex: Int, weight: String) {
        _uiState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val sets = exercise.sets.toMutableList()
            sets[setIndex] = sets[setIndex].copy(weight = weight)
            exercises[exerciseIndex] = exercise.copy(sets = sets)
            state.copy(exercises = exercises)
        }
    }

    fun updateSetReps(exerciseIndex: Int, setIndex: Int, reps: String) {
        _uiState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val sets = exercise.sets.toMutableList()
            sets[setIndex] = sets[setIndex].copy(reps = reps)
            exercises[exerciseIndex] = exercise.copy(sets = sets)
            state.copy(exercises = exercises)
        }
    }

    fun addExercise(exercise: Exercise) {
        _uiState.update { state ->
            val newExerciseWithSets = ExerciseWithSets(
                exercise = exercise.copy(order = state.exercises.size),
                sets = listOf(
                    SetUiState(
                        setNumber = "1",
                        previous = "",
                        weight = "0",
                        reps = "10",
                        type = SetType.NORMAL
                    )
                )
            )
            state.copy(exercises = state.exercises + newExerciseWithSets)
        }
    }

    fun finishWorkout() {
        timerJob?.cancel()
        endTime = kotlin.time.Clock.System.now()

        val state = _uiState.value
        // Просто маппим UI данные в доменные модели, не трогаем id
        val exercises = state.exercises.map { exWithSets ->
            exWithSets.exercise.copy(
                sets = exWithSets.sets.map { setUi ->
                    Set(
                        id = 0, // Room сам сгенерирует
                        rest = 90.seconds,
                        load = setUi.weight.toFloatOrNull() ?: 0f,
                        reps = setUi.reps.toIntOrNull() ?: 0,
                        type = setUi.type,
                        isCompleted = setUi.isCompleted
                    )
                }
            )
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val template = currentTemplate?.let {
                it.copy(
                    exercise = exercises,
                    useCount = it.useCount + 1
                )
            } ?: WorkoutTemplate(
                id = 0,
                name = state.templateName,
                useCount = 1,
                exercise = exercises,
                isDeleted = false
            )

            if (currentTemplate != null && currentTemplate!!.id != 0) {
                updateWorkoutTemplate(currentTemplate!!.id.toString(), template)
            }

            val workout = Workout(
                id = System.currentTimeMillis().toString(),
                template = template,
                startTime = startTime,
                endTime = endTime,
                exercises = exercises,
                note = "",
                date = LocalDate.now()
            )

            createWorkout(workout)
                .onSuccess { saved ->
                    _uiState.update {
                        it.copy(
                            isRunning = false,
                            isFinished = true,
                            isLoading = false,
                            workoutId = saved.id
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }
}