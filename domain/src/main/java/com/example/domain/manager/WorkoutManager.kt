package com.example.domain.manager

import com.example.domain.model.workout.Exercise
import com.example.domain.model.workout.Set
import com.example.domain.model.workout.SetType
import com.example.domain.model.workout.Workout
import com.example.domain.model.workout.WorkoutTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutManager @Inject constructor() {


    data class RestProgress(
        val remainingSeconds: Int = 0,
        val totalSeconds: Int = 0,
        val isRunning: Boolean = false
    ) {
        val progress: Float
            get() = if (totalSeconds > 0) (totalSeconds - remainingSeconds).toFloat() / totalSeconds else 0f

        val formattedTime: String
            get() = String.format("%d:%02d", remainingSeconds / 60, remainingSeconds % 60)

        val formattedConfiguredTime: String
            get() = String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60)
    }

    data class ActiveRestTimer(
        val exerciseIndex: Int,
        val setIndex: Int,
        val formattedTime: String,
        val progress: RestProgress,
        val isRunning: Boolean = true
    )

    data class ExerciseWithSets(
        val exercise: Exercise,
        val sets: List<SetData>
    )

    data class SetData(
        val setNumber: String,
        val previous: String,
        val weight: String,
        val reps: String,
        val isCompleted: Boolean = false,
        val restProgress: RestProgress = RestProgress(),
        val restDuration: Duration = 90.seconds,
        val type: SetType = SetType.NORMAL
    )

    data class WorkoutState(
        val templateName: String = "",
        val exercises: List<ExerciseWithSets> = emptyList(),
        val elapsedTimeFormatted: String = "00:00",
        val isFinished: Boolean = false,
        val showFinishDialog: Boolean = false,
        val activeRestTimer: ActiveRestTimer? = null
    )

    private val _workoutState = MutableStateFlow(WorkoutState())
    val workoutState: StateFlow<WorkoutState> = _workoutState.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var elapsedTimeJob: kotlinx.coroutines.Job? = null
    private var restTimerJob: kotlinx.coroutines.Job? = null
    private var startTime: Instant? = null
    var currentTemplate: WorkoutTemplate? = null

    private val _completedWorkouts = MutableStateFlow<List<Workout>>(emptyList())
    val completedWorkouts: StateFlow<List<Workout>> = _completedWorkouts.asStateFlow()

    private val _templateCache = mutableMapOf<Int, WorkoutTemplate>()
    val templateCache: Map<Int, WorkoutTemplate> get() = _templateCache

    fun hasActiveWorkout(): Boolean = _workoutState.value.exercises.isNotEmpty() && !isFinished()

    private fun isFinished(): Boolean = _workoutState.value.isFinished

    fun startWorkout(template: WorkoutTemplate) {
        val effectiveTemplate = _templateCache[template.id] ?: template
        currentTemplate = effectiveTemplate
        startTime = Clock.System.now()
        val exercises = effectiveTemplate.exercise.map { exercise ->
            ExerciseWithSets(
                exercise = exercise,
                sets = exercise.sets.mapIndexed { index, set ->
                    SetData(
                        setNumber = when (set.type) {
                            SetType.WARMUP -> "W"
                            SetType.FAILURE -> "F"
                            else -> (index + 1).toString()
                        },
                        previous = "${set.load.toInt()} x ${set.reps}",
                        weight = set.load.toString(),
                        reps = set.reps.toString(),
                        restDuration = set.rest,
                        type = set.type
                    )
                }
            )
        }

        _workoutState.value = WorkoutState(
            templateName = effectiveTemplate.name,
            exercises = exercises
        )

        startElapsedTimer()
    }

    fun startFreeWorkout() {
        currentTemplate = null
        startTime = Clock.System.now()
        _workoutState.value = WorkoutState(
            templateName = "Свободная тренировка",
            exercises = emptyList()
        )
        startElapsedTimer()
    }

    fun addExercise(exercise: Exercise) {
        _workoutState.update { state ->
            val sets = if (exercise.sets.isEmpty()) {
                listOf(
                    SetData(
                        setNumber = "1",
                        previous = "",
                        weight = "0",
                        reps = "10",
                        restDuration = 90.seconds,
                        type = SetType.NORMAL
                    )
                )
            } else {
                exercise.sets.mapIndexed { index, set ->
                    SetData(
                        setNumber = when (set.type) {
                            SetType.WARMUP -> "W"
                            SetType.FAILURE -> "F"
                            else -> (index + 1).toString()
                        },
                        previous = "${set.load.toInt()} x ${set.reps}",
                        weight = set.load.toString(),
                        reps = set.reps.toString(),
                        restDuration = set.rest,
                        type = set.type
                    )
                }
            }
            val newExercise = ExerciseWithSets(
                exercise = exercise,
                sets = sets
            )
            state.copy(exercises = state.exercises + newExercise)
        }
    }

    fun addSet(exerciseIndex: Int) {
        _workoutState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val lastSet = exercise.sets.lastOrNull()
            val newSet = SetData(
                setNumber = (exercise.sets.size + 1).toString(),
                previous = lastSet?.let { "${it.weight} x ${it.reps}" } ?: "",
                weight = lastSet?.weight ?: "0",
                reps = lastSet?.reps ?: "10",
                restDuration = lastSet?.restDuration ?: 90.seconds,
                type = SetType.NORMAL
            )
            exercises[exerciseIndex] = exercise.copy(sets = exercise.sets + newSet)
            state.copy(exercises = exercises)
        }
    }

    fun removeSet(exerciseIndex: Int, setIndex: Int) {
        _workoutState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val updatedSets = exercise.sets.toMutableList().apply { removeAt(setIndex) }
                .mapIndexed { i, set ->
                    set.copy(
                        setNumber = when (set.type) {
                            SetType.WARMUP -> "W"
                            SetType.FAILURE -> "F"
                            else -> (i + 1).toString()
                        }
                    )
                }
            exercises[exerciseIndex] = exercise.copy(sets = updatedSets)
            state.copy(exercises = exercises)
        }
    }

    fun changeSetType(exerciseIndex: Int, setIndex: Int, type: SetType) {
        _workoutState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val sets = exercise.sets.toMutableList()
            val set = sets[setIndex]
            sets[setIndex] = set.copy(
                type = type,
                setNumber = when (type) {
                    SetType.WARMUP -> "W"
                    SetType.FAILURE -> "F"
                    else -> (setIndex + 1).toString()
                }
            )
            exercises[exerciseIndex] = exercise.copy(sets = sets)
            state.copy(exercises = exercises)
        }
    }

    fun startRestTimer(exerciseIndex: Int, setIndex: Int) {
        stopRestTimer()
        val exercise = _workoutState.value.exercises.getOrNull(exerciseIndex) ?: return
        val set = exercise.sets.getOrNull(setIndex) ?: return
        val totalSeconds = set.restDuration.inWholeSeconds.toInt()

        _workoutState.update { state ->
            val exercises = state.exercises.toMutableList()
            val ex = exercises[exerciseIndex]
            val sets = ex.sets.toMutableList()
            sets[setIndex] = sets[setIndex].copy(
                restProgress = RestProgress(
                    remainingSeconds = totalSeconds,
                    totalSeconds = totalSeconds,
                    isRunning = true
                )
            )
            exercises[exerciseIndex] = ex.copy(sets = sets)
            state.copy(
                exercises = exercises,
                activeRestTimer = ActiveRestTimer(
                    exerciseIndex = exerciseIndex,
                    setIndex = setIndex,
                    formattedTime = RestProgress(totalSeconds, totalSeconds, true).formattedTime,
                    progress = RestProgress(totalSeconds, totalSeconds, true),
                    isRunning = true
                )
            )
        }

        restTimerJob = scope.launch {
            var remaining = totalSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _workoutState.update { state ->
                    val exercises = state.exercises.toMutableList()
                    val ex = exercises[exerciseIndex]
                    val sets = ex.sets.toMutableList()
                    sets[setIndex] = sets[setIndex].copy(
                        restProgress = RestProgress(
                            remainingSeconds = remaining,
                            totalSeconds = totalSeconds,
                            isRunning = true
                        )
                    )
                    exercises[exerciseIndex] = ex.copy(sets = sets)
                    state.copy(
                        exercises = exercises,
                        activeRestTimer = ActiveRestTimer(
                            exerciseIndex = exerciseIndex,
                            setIndex = setIndex,
                            formattedTime = RestProgress(remaining, totalSeconds, true).formattedTime,
                            progress = RestProgress(remaining, totalSeconds, true),
                            isRunning = true
                        )
                    )
                }
            }
            _workoutState.update { state ->
                val exercises = state.exercises.toMutableList()
                val ex = exercises[exerciseIndex]
                val sets = ex.sets.toMutableList()
                sets[setIndex] = sets[setIndex].copy(
                    restProgress = RestProgress(0, totalSeconds, false)
                )
                exercises[exerciseIndex] = ex.copy(sets = sets)
                state.copy(
                    exercises = exercises,
                    activeRestTimer = null
                )
            }
        }
    }

    fun toggleSetCompletion(exerciseIndex: Int, setIndex: Int) {
        val exercise = _workoutState.value.exercises.getOrNull(exerciseIndex) ?: return
        val set = exercise.sets.getOrNull(setIndex) ?: return

        if (set.isCompleted) {
            _workoutState.update { state ->
                val exercises = state.exercises.toMutableList()
                val ex = exercises[exerciseIndex]
                val sets = ex.sets.toMutableList()
                sets[setIndex] = sets[setIndex].copy(
                    isCompleted = false,
                    restProgress = RestProgress()
                )
                exercises[exerciseIndex] = ex.copy(sets = sets)
                state.copy(exercises = exercises)
            }
        } else {
            _workoutState.update { state ->
                val exercises = state.exercises.toMutableList()
                val ex = exercises[exerciseIndex]
                val sets = ex.sets.toMutableList()
                sets[setIndex] = sets[setIndex].copy(isCompleted = true)
                exercises[exerciseIndex] = ex.copy(sets = sets)
                state.copy(exercises = exercises)
            }
            startRestTimer(exerciseIndex, setIndex)
        }
    }

    fun skipRestTimer(exerciseIndex: Int, setIndex: Int) {
        stopRestTimer()
        _workoutState.update { state ->
            val exercises = state.exercises.toMutableList()
            val ex = exercises[exerciseIndex]
            val sets = ex.sets.toMutableList()
            sets[setIndex] = sets[setIndex].copy(
                restProgress = RestProgress()
            )
            exercises[exerciseIndex] = ex.copy(sets = sets)
            state.copy(
                exercises = exercises,
                activeRestTimer = null
            )
        }
    }

    fun adjustRestTime(exerciseIndex: Int, setIndex: Int, deltaSeconds: Int) {
        _workoutState.update { state ->
            val exercises = state.exercises.toMutableList()
            val ex = exercises[exerciseIndex]
            val sets = ex.sets.toMutableList()
            val currentDuration = sets[setIndex].restDuration
            val newDuration = (currentDuration + deltaSeconds.seconds).coerceAtLeast(0.seconds)
            sets[setIndex] = sets[setIndex].copy(restDuration = newDuration)
            exercises[exerciseIndex] = ex.copy(sets = sets)
            state.copy(exercises = exercises)
        }
    }

    fun updateSetWeight(exerciseIndex: Int, setIndex: Int, weight: String) {
        _workoutState.update { state ->
            val exercises = state.exercises.toMutableList()
            val ex = exercises[exerciseIndex]
            val sets = ex.sets.toMutableList()
            sets[setIndex] = sets[setIndex].copy(weight = weight)
            exercises[exerciseIndex] = ex.copy(sets = sets)
            state.copy(exercises = exercises)
        }
    }

    fun updateSetReps(exerciseIndex: Int, setIndex: Int, reps: String) {
        _workoutState.update { state ->
            val exercises = state.exercises.toMutableList()
            val ex = exercises[exerciseIndex]
            val sets = ex.sets.toMutableList()
            sets[setIndex] = sets[setIndex].copy(reps = reps)
            exercises[exerciseIndex] = ex.copy(sets = sets)
            state.copy(exercises = exercises)
        }
    }

    fun finishWorkout() {
        val hasUncompleted = _workoutState.value.exercises.any { ex ->
            ex.sets.any { !it.isCompleted }
        }
        if (hasUncompleted) {
            _workoutState.update { it.copy(showFinishDialog = true) }
        } else {
            completeFinish()
        }
    }

    fun dismissFinishDialog() {
        _workoutState.update { it.copy(showFinishDialog = false) }
    }

    fun markAllSetsCompletedAndFinish() {
        _workoutState.update { state ->
            state.copy(
                exercises = state.exercises.map { ex ->
                    ex.copy(sets = ex.sets.map { it.copy(isCompleted = true) })
                }
            )
        }
        completeFinish()
    }

    fun removeUncompletedSetsAndFinish() {
        _workoutState.update { state ->
            state.copy(
                exercises = state.exercises.map { ex ->
                    ex.copy(sets = ex.sets.filter { it.isCompleted })
                }.filter { it.sets.isNotEmpty() }
            )
        }
        completeFinish()
    }

    private fun completeFinish() {
        stopRestTimer()
        stopElapsedTimer()

        val template = currentTemplate ?: WorkoutTemplate(
            id = 0,
            name = _workoutState.value.templateName,
            useCount = 0,
            exercise = _workoutState.value.exercises.map { it.exercise },
            isDeleted = false
        )

        val workout = Workout(
            id = System.currentTimeMillis().toString(),
            template = template,
            startTime = startTime,
            endTime = Clock.System.now(),
            exercises = _workoutState.value.exercises.map { ews ->
                Exercise(
                    id = ews.exercise.id,
                    template = ews.exercise.template,
                    note = ews.exercise.note,
                    sets = ews.sets.mapIndexed { index, setData ->
                        Set(
                            id = index,
                            rest = setData.restDuration,
                            load = setData.weight.toFloatOrNull() ?: 0f,
                            reps = setData.reps.toIntOrNull() ?: 0,
                            type = setData.type,
                            isCompleted = setData.isCompleted
                        )
                    },
                    order = ews.exercise.order
                )
            },
            date = LocalDate.now()
        )

        _completedWorkouts.value = _completedWorkouts.value + workout

        currentTemplate?.let { t ->
            val updatedExercises = _workoutState.value.exercises.map { ews ->
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
            _templateCache[t.id] = t.copy(
                exercise = updatedExercises,
                useCount = t.useCount + 1
            )
        }

        _workoutState.update { it.copy(isFinished = true, showFinishDialog = false) }
    }

    private fun startElapsedTimer() {
        elapsedTimeJob?.cancel()
        elapsedTimeJob = scope.launch {
            while (true) {
                delay(1000)
                val elapsed = startTime?.let { Clock.System.now() - it } ?: 0.seconds
                val minutes = elapsed.inWholeMinutes
                val seconds = elapsed.inWholeSeconds % 60
                _workoutState.update {
                    it.copy(elapsedTimeFormatted = String.format("%02d:%02d", minutes, seconds))
                }
            }
        }
    }

    private fun stopElapsedTimer() {
        elapsedTimeJob?.cancel()
    }

    private fun stopRestTimer() {
        restTimerJob?.cancel()
    }

    fun clearWorkout() {
        stopRestTimer()
        stopElapsedTimer()
        _workoutState.value = WorkoutState()
        startTime = null
        currentTemplate = null
    }
}
