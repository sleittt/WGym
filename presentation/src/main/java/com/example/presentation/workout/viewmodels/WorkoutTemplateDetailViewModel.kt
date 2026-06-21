package com.example.presentation.workout.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.workout.Exercise
import com.example.domain.model.workout.ExerciseTemplate
import com.example.domain.model.workout.Set
import com.example.domain.model.workout.SetType
import com.example.domain.model.workout.WorkoutTemplate
import com.example.domain.usecase.workout.CreateWorkoutTemplateUseCase
import com.example.domain.usecase.workout.DeleteWorkoutTemplateUseCase
import com.example.domain.usecase.workout.GetExerciseTemplateByIdUseCase
import com.example.domain.usecase.workout.GetWorkoutTemplateByIdUseCase
import com.example.domain.usecase.workout.ToggleWorkoutTemplatePinUseCase
import com.example.domain.usecase.workout.UpdateWorkoutTemplateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class WorkoutTemplateDetailViewModel @Inject constructor(
    private val getWorkoutTemplateById: GetWorkoutTemplateByIdUseCase,
    private val getExerciseTemplateById: GetExerciseTemplateByIdUseCase,
    private val createWorkoutTemplate: CreateWorkoutTemplateUseCase,
    private val updateWorkoutTemplate: UpdateWorkoutTemplateUseCase,
    private val deleteWorkoutTemplate: DeleteWorkoutTemplateUseCase,
    private val togglePin: ToggleWorkoutTemplatePinUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    data class UiState(
        val templateId: Int = 0,
        val name: String = "",
        val exercises: List<Exercise> = emptyList(),
        val useCount: Int = 0,
        val isPinned: Boolean = false,
        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val error: String? = null,
        val isNew: Boolean = true,
        val saved: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val navTemplateId: String = savedStateHandle.get<String>("templateId") ?: "0"

    init {
        if (navTemplateId == "0" || navTemplateId.isBlank()) {
            _uiState.update { it.copy(templateId = 0, name = "Новый шаблон", isNew = true) }
        } else {
            loadTemplate(navTemplateId)
        }
    }

    private fun loadTemplate(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val template = getWorkoutTemplateById(id).first()
                if (template != null) {
                    _uiState.update {
                        it.copy(
                            templateId = template.id,
                            name = template.name,
                            exercises = template.exercise,
                            useCount = template.useCount,
                            isPinned = template.isPinned,
                            isNew = false,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(error = "Шаблон не найден", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun fetchAndAddExercise(exerciseTemplateId: String) {
        viewModelScope.launch {
            val template = getExerciseTemplateById(exerciseTemplateId)
            if (template != null) {
                addExercise(template)
            }
        }
    }

    fun addExercise(exerciseTemplate: ExerciseTemplate) {
        _uiState.update { state ->
            val newExercise = Exercise(
                id = state.exercises.size,
                template = exerciseTemplate,
                note = "",
                sets = listOf(
                    Set(id = 0, rest = 90.seconds, load = 0f, reps = 10, type = SetType.NORMAL)
                ),
                order = state.exercises.size
            )
            state.copy(exercises = state.exercises + newExercise)
        }
    }

    fun removeExercise(index: Int) {
        _uiState.update { state ->
            val updated = state.exercises.toMutableList().apply { removeAt(index) }
                .mapIndexed { i, ex -> ex.copy(order = i) }
            state.copy(exercises = updated)
        }
    }

    fun addSet(exerciseIndex: Int) {
        _uiState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val lastSet = exercise.sets.lastOrNull()
            val newSet = Set(
                id = exercise.sets.size,
                rest = 90.seconds,
                load = lastSet?.load ?: 0f,
                reps = lastSet?.reps ?: 10,
                type = SetType.NORMAL
            )
            exercises[exerciseIndex] = exercise.copy(sets = exercise.sets + newSet)
            state.copy(exercises = exercises)
        }
    }

    fun removeSet(exerciseIndex: Int, setIndex: Int) {
        _uiState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val sets = exercise.sets.toMutableList().apply { removeAt(setIndex) }
            exercises[exerciseIndex] = exercise.copy(sets = sets)
            state.copy(exercises = exercises)
        }
    }

    fun updateSet(exerciseIndex: Int, setIndex: Int, load: Float, reps: Int) {
        _uiState.update { state ->
            val exercises = state.exercises.toMutableList()
            val exercise = exercises[exerciseIndex]
            val sets = exercise.sets.toMutableList()
            sets[setIndex] = sets[setIndex].copy(load = load, reps = reps)
            exercises[exerciseIndex] = exercise.copy(sets = sets)
            state.copy(exercises = exercises)
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Название не может быть пустым") }
            return
        }
        if (state.exercises.isEmpty()) {
            _uiState.update { it.copy(error = "Добавьте хотя бы одно упражнение") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val template = WorkoutTemplate(
                id = state.templateId,
                name = state.name,
                useCount = state.useCount,
                exercise = state.exercises,
                isDeleted = false
            )
            val result = if (state.isNew) {
                createWorkoutTemplate(template)
            } else {
                updateWorkoutTemplate(state.templateId.toString(), template)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, saved = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isSaving = false) }
                }
        }
    }

    fun delete() {
        if (!_uiState.value.isNew) {
            viewModelScope.launch {
                deleteWorkoutTemplate(_uiState.value.templateId.toString())
            }
        }
    }

    fun duplicate() {
        // TODO: implement duplicate
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun consumeSaved() {
        _uiState.update { it.copy(saved = false) }
    }

    fun togglePin() {
        val state = _uiState.value
        if (state.isNew) return
        viewModelScope.launch {
            togglePin(state.templateId.toString(), !state.isPinned)
            _uiState.update { it.copy(isPinned = !it.isPinned) }
        }
    }
}