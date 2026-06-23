package com.example.domain.manager

import com.example.domain.model.workout.Exercise
import com.example.domain.model.workout.Set
import com.example.domain.model.workout.SetType
import com.example.domain.model.workout.Workout
import com.example.domain.model.workout.WorkoutTemplate
import com.example.domain.usecase.workout.CreateWorkoutUseCase
import com.example.domain.usecase.workout.UpdateWorkoutTemplateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Clock
import kotlin.time.Instant

@Singleton
class WorkoutManager @Inject constructor(
    private val createWorkout: CreateWorkoutUseCase,
    private val updateWorkoutTemplate: UpdateWorkoutTemplateUseCase
) {
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
        val type: SetType = SetType.NORMAL,
        val restDuration: Duration = 90.seconds,
        val restProgress: RestProgress = RestProgress()
    )

    data class RestProgress(
        val remainingSeconds: Int = 90,
        val totalSeconds: Int = 90,
        val isRunning: Boolean = false
    ) {
        val progress: Float
            get() = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 0f

        val formattedTime: String
            get() = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60)

        val formattedConfiguredTime: String
            get() = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60)
    }

    data class ActiveRestTimer(
        val exerciseIndex: Int,
        val setIndex: Int,
        val exerciseName: String,
        val setNumber: String,
        val progress: RestProgress
    ) {
        val isRunning: Boolean get() = progress.isRunning && progress.remainingSeconds > 0
        val formattedTime: String get() = progress.formattedTime
    }

    data class WorkoutState(
        val templateName: String = "",
        val exercises: List<ExerciseWithSets> = emptyList(),
        val isRunning: Boolean = false,
        val elapsedSeconds: Long = 0,
        val isLoading: Boolean = false,
        val error: String? = null,
        val canFinish: Boolean = false,
        val isFinished: Boolean = false,
        val workoutId: String? = null,
        val defaultRestTime: Duration = 90.seconds,
        val activeRestTimer: ActiveRestTimer? = null,
        val showFinishDialog: Boolean = false
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

    private val _workoutState = MutableStateFlow(WorkoutState())
    val workoutState: StateFlow<WorkoutState> = _workoutState.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timerJob: Job? = null
    private var restTimerJob: Job? = null
    private var startTime: Instant? = null
    private var endTime: Instant? = null
    private var currentTemplate: WorkoutTemplate? = null
    private var activeRestTimerIndices: Pair<Int, Int>? = null

    fun hasActiveWorkout(): Boolean {
        return _workoutState.value.isRunning && !_workoutState.value.isFinished
    }

    fun startWorkout(template: WorkoutTemplate) {
        if (_workoutState.value.isRunning) return

        startTime = Clock.System.now()
        val defaultRest = template.defaultRestTime
        currentTemplate = template

        _workoutState.value = WorkoutState(
            templateName = template.name,
            defaultRestTime = defaultRest,
            exercises = template.exercise.map { ex ->
                ExerciseWithSets(
                    exercise = ex,
                    sets = ex.sets.mapIndexed { index, set ->
                        val restSec = set.rest.inWholeSeconds.toInt()
                        SetUiState(
                            setNumber = when {
                                set.type == SetType.WARMUP -> "W"
                                index == ex.sets.lastIndex && set.type == SetType.FAILURE -> "F"
                                else -> (index + 1).toString()
                            },
                            previous = "${set.load.toInt()} кг x ${set.reps}",
                            weight = set.load.toString(),
                            reps = set.reps.toString(),
                            type = set.type,
                            restDuration = set.rest,
                            restProgress = RestProgress(
                                remainingSeconds = restSec,
                                totalSeconds = restSec,
                                isRunning = false
                            )
                        )
                    }
                )
            },
            isRunning = true,
            elapsedSeconds = 0,
            isLoading = false
        )
        startTimer()
    }

    fun startFreeWorkout() {
        if (_workoutState.value.isRunning) return

        startTime = Clock.System.now()
        _workoutState.value = WorkoutState(
            templateName = "Свободная тренировка",
            isRunning = true,
            elapsedSeconds = 0
        )
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive) {
                delay(1000)
                _workoutState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    private fun updateActiveRestTimer(exerciseIndex: Int, setIndex: Int, progress: RestProgress) {
        val exercises = _workoutState.value.exercises
        if (exerciseIndex < exercises.size) {
            val exercise = exercises[exerciseIndex]
            if (setIndex < exercise.sets.size) {
                val set = exercise.sets[setIndex]
                _workoutState.update { state ->
                    state.copy(
                        activeRestTimer = ActiveRestTimer(
                            exerciseIndex = exerciseIndex,
                            setIndex = setIndex,
                            exerciseName = exercise.exercise.template.name,
                            setNumber = set.setNumber,
                            progress = progress
                        )
                    )
                }
            }
        }
    }

    fun toggleSetCompletion(exerciseIndex: Int, setIndex: Int) {
        val state = _workoutState.value
        if (!state.isRunning || state.isFinished) return

        val exercises = state.exercises.toMutableList()
        if (exerciseIndex >= exercises.size) return
        val exercise = exercises[exerciseIndex]
        val sets = exercise.sets.toMutableList()
        if (setIndex >= sets.size) return
        val set = sets[setIndex]

        val newCompleted = !set.isCompleted

        restTimerJob?.cancel()
        activeRestTimerIndices = null

        if (newCompleted) {
            val restDuration = set.restDuration
            val totalSeconds = restDuration.inWholeSeconds.toInt()

            sets[setIndex] = set.copy(
                isCompleted = true,
                restTime = restDuration,
                restProgress = RestProgress(
                    remainingSeconds = totalSeconds,
                    totalSeconds = totalSeconds,
                    isRunning = true
                )
            )

            _workoutState.value = state.copy(
                exercises = exercises.mapIndexed { i, ex ->
                    if (i == exerciseIndex) ex.copy(sets = sets) else ex
                },
                canFinish = true,
                activeRestTimer = null
            )

            startRestTimer(exerciseIndex, setIndex, totalSeconds)
        } else {
            val totalSeconds = set.restDuration.inWholeSeconds.toInt()
            sets[setIndex] = set.copy(
                isCompleted = false,
                restTime = null,
                restProgress = RestProgress(
                    remainingSeconds = totalSeconds,
                    totalSeconds = totalSeconds,
                    isRunning = false
                )
            )

            val hasAnyCompleted = exercises.any { ex -> ex.sets.any { it.isCompleted } }

            _workoutState.value = state.copy(
                exercises = exercises.mapIndexed { i, ex ->
                    if (i == exerciseIndex) ex.copy(sets = sets) else ex
                },
                canFinish = hasAnyCompleted,
                activeRestTimer = null
            )
        }
    }

    private fun startRestTimer(exerciseIndex: Int, setIndex: Int, totalSeconds: Int) {
        restTimerJob?.cancel()
        activeRestTimerIndices = Pair(exerciseIndex, setIndex)

        updateActiveRestTimer(
            exerciseIndex, setIndex,
            RestProgress(remainingSeconds = totalSeconds, totalSeconds = totalSeconds, isRunning = true)
        )

        restTimerJob = scope.launch {
            var remaining = totalSeconds
            while (isActive && remaining > 0) {
                delay(1000)
                remaining--

                val progress = RestProgress(
                    remainingSeconds = remaining,
                    totalSeconds = totalSeconds,
                    isRunning = remaining > 0
                )

                _workoutState.update { state ->
                    val exercises = state.exercises.toMutableList()
                    val exercise = exercises[exerciseIndex]
                    val sets = exercise.sets.toMutableList()
                    val set = sets[setIndex]

                    sets[setIndex] = set.copy(restProgress = progress)
                    exercises[exerciseIndex] = exercise.copy(sets = sets)

                    val newActiveTimer = state.activeRestTimer?.let {
                        if (it.exerciseIndex == exerciseIndex && it.setIndex == setIndex) {
                            it.copy(progress = progress)
                        } else it
                    }

                    state.copy(exercises = exercises, activeRestTimer = newActiveTimer)
                }
            }

            if (remaining <= 0) {
                val finalProgress = RestProgress(
                    remainingSeconds = 0,
                    totalSeconds = totalSeconds,
                    isRunning = false
                )

                _workoutState.update { state ->
                    val exercises = state.exercises.toMutableList()
                    val exercise = exercises[exerciseIndex]
                    val sets = exercise.sets.toMutableList()
                    val set = sets[setIndex]

                    sets[setIndex] = set.copy(restProgress = finalProgress)
                    exercises[exerciseIndex] = exercise.copy(sets = sets)

                    val newActiveTimer = state.activeRestTimer?.let {
                        if (it.exerciseIndex == exerciseIndex && it.setIndex == setIndex) {
                            it.copy(progress = finalProgress)
                        } else it
                    }

                    state.copy(exercises = exercises, activeRestTimer = newActiveTimer)
                }
            }
        }
    }

    fun skipRestTimer(exerciseIndex: Int, setIndex: Int) {
        restTimerJob?.cancel()
        activeRestTimerIndices = null

        _workoutState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val sets = exercise.sets.toMutableList()
            val set = sets[setIndex]

            val totalSeconds = set.restDuration.inWholeSeconds.toInt()
            val finalProgress = RestProgress(
                remainingSeconds = 0,
                totalSeconds = totalSeconds,
                isRunning = false
            )

            sets[setIndex] = set.copy(restProgress = finalProgress)
            exercises[exerciseIndex] = exercise.copy(sets = sets)

            val newActiveTimer = state.activeRestTimer?.let {
                if (it.exerciseIndex == exerciseIndex && it.setIndex == setIndex) null else it
            }

            state.copy(exercises = exercises, activeRestTimer = newActiveTimer)
        }
    }

    fun adjustRestTime(exerciseIndex: Int, setIndex: Int, deltaSeconds: Int) {
        _workoutState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val sets = exercise.sets.toMutableList()
            val set = sets[setIndex]

            val currentDuration = set.restDuration.inWholeSeconds.toInt()
            val newDuration = (currentDuration + deltaSeconds).coerceAtLeast(15)
            val newRestDuration = newDuration.seconds

            val wasRunning = set.restProgress.isRunning

            val newProgress = if (wasRunning) {
                RestProgress(
                    remainingSeconds = newDuration,
                    totalSeconds = newDuration,
                    isRunning = true
                )
            } else {
                RestProgress(
                    remainingSeconds = newDuration,
                    totalSeconds = newDuration,
                    isRunning = false
                )
            }

            sets[setIndex] = set.copy(
                restDuration = newRestDuration,
                restProgress = newProgress
            )
            exercises[exerciseIndex] = exercise.copy(sets = sets)

            val newActiveTimer = state.activeRestTimer?.let {
                if (it.exerciseIndex == exerciseIndex && it.setIndex == setIndex) {
                    it.copy(progress = newProgress)
                } else it
            }

            state.copy(exercises = exercises, activeRestTimer = newActiveTimer)
        }

        if (activeRestTimerIndices == Pair(exerciseIndex, setIndex)) {
            val state = _workoutState.value
            val set = state.exercises[exerciseIndex].sets[setIndex]
            startRestTimer(exerciseIndex, setIndex, set.restDuration.inWholeSeconds.toInt())
        }
    }

    fun updateSetWeight(exerciseIndex: Int, setIndex: Int, weight: String) {
        _workoutState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val sets = exercise.sets.toMutableList()
            sets[setIndex] = sets[setIndex].copy(weight = weight)
            exercises[exerciseIndex] = exercise.copy(sets = sets)
            state.copy(exercises = exercises)
        }
    }

    fun updateSetReps(exerciseIndex: Int, setIndex: Int, reps: String) {
        _workoutState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val sets = exercise.sets.toMutableList()
            sets[setIndex] = sets[setIndex].copy(reps = reps)
            exercises[exerciseIndex] = exercise.copy(sets = sets)
            state.copy(exercises = exercises)
        }
    }

    fun addExercise(exercise: Exercise) {
        _workoutState.update { state ->
            val restSec = state.defaultRestTime.inWholeSeconds.toInt()
            val newExerciseWithSets = ExerciseWithSets(
                exercise = exercise.copy(order = state.exercises.size),
                sets = listOf(
                    SetUiState(
                        setNumber = "1",
                        previous = "",
                        weight = "0",
                        reps = "10",
                        type = SetType.NORMAL,
                        restDuration = state.defaultRestTime,
                        restProgress = RestProgress(
                            remainingSeconds = restSec,
                            totalSeconds = restSec,
                            isRunning = false
                        )
                    )
                )
            )
            state.copy(exercises = state.exercises + newExerciseWithSets)
        }
    }

    // --- Finish Dialog Logic ---

    fun requestFinishWorkout() {
        val state = _workoutState.value
        if (!state.isRunning || state.isFinished) return

        // Проверяем, все ли подходы выполнены
        val allSetsCompleted = state.exercises.all { ex ->
            ex.sets.all { it.isCompleted }
        }

        if (allSetsCompleted) {
            // Все выполнены - сразу завершаем
            doFinishWorkout()
        } else {
            // Не все выполнены - показываем диалог
            _workoutState.update { it.copy(showFinishDialog = true) }
        }
    }

    fun dismissFinishDialog() {
        _workoutState.update { it.copy(showFinishDialog = false) }
    }

    fun markAllSetsCompletedAndFinish() {
        _workoutState.update { state ->
            val updatedExercises = state.exercises.map { ex ->
                ex.copy(
                    sets = ex.sets.map { set ->
                        set.copy(isCompleted = true)
                    }
                )
            }
            state.copy(
                exercises = updatedExercises,
                canFinish = true,
                showFinishDialog = false
            )
        }
        doFinishWorkout()
    }

    fun removeUncompletedSetsAndFinish() {
        _workoutState.update { state ->
            val updatedExercises = state.exercises.map { ex ->
                ex.copy(
                    sets = ex.sets.filter { it.isCompleted }
                )
            }.filter { it.sets.isNotEmpty() } // Удаляем упражнения без подходов
            state.copy(
                exercises = updatedExercises,
                canFinish = updatedExercises.isNotEmpty(),
                showFinishDialog = false
            )
        }
        doFinishWorkout()
    }

    fun finishWorkout() {
        requestFinishWorkout()
    }

    private fun doFinishWorkout() {
        timerJob?.cancel()
        restTimerJob?.cancel()
        endTime = Clock.System.now()

        val state = _workoutState.value
        val exercises = state.exercises.map { exWithSets ->
            exWithSets.exercise.copy(
                sets = exWithSets.sets.map { setUi ->
                    Set(
                        id = 0,
                        rest = setUi.restDuration,
                        load = setUi.weight.toFloatOrNull() ?: 0f,
                        reps = setUi.reps.toIntOrNull() ?: 0,
                        type = setUi.type,
                        isCompleted = setUi.isCompleted
                    )
                }
            )
        }

        scope.launch {
            _workoutState.update { it.copy(isLoading = true) }

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
                    _workoutState.update {
                        it.copy(
                            isRunning = false,
                            isFinished = true,
                            isLoading = false,
                            workoutId = saved.id,
                            activeRestTimer = null,
                            showFinishDialog = false
                        )
                    }
                }
                .onFailure { e ->
                    _workoutState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun resetWorkout() {
        timerJob?.cancel()
        restTimerJob?.cancel()
        startTime = null
        endTime = null
        currentTemplate = null
        activeRestTimerIndices = null
        _workoutState.value = WorkoutState()
    }
}