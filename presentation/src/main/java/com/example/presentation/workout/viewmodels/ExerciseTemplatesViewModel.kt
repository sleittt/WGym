package com.example.presentation.workout.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.workout.ExerciseTemplate
import com.example.domain.model.workout.MuscleGroup
import com.example.domain.usecase.workout.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseTemplatesViewModel @Inject constructor(
    private val getExerciseTemplates: GetExerciseTemplatesUseCase,
    private val getExerciseTemplateById: GetExerciseTemplateByIdUseCase,
    private val createExerciseTemplate: CreateExerciseTemplateUseCase,
    private val updateExerciseTemplate: UpdateExerciseTemplateUseCase,
    private val deleteExerciseTemplate: DeleteExerciseTemplateUseCase
) : ViewModel() {

    data class UiState(
        val templates: List<ExerciseTemplate> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedTemplate: ExerciseTemplate? = null,
        val isEditDialogOpen: Boolean = false,
        val filterMuscleGroup: MuscleGroup? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        getExerciseTemplates()
            .onEach { templates ->
                _uiState.update { it.copy(templates = templates, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun selectTemplate(template: ExerciseTemplate?) {
        _uiState.update { it.copy(selectedTemplate = template, isEditDialogOpen = template != null) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(isEditDialogOpen = false, selectedTemplate = null) }
    }

    fun createTemplate(name: String, description: String, muscleGroups: List<MuscleGroup>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val template = ExerciseTemplate(
                id = 0, name = name, description = description,
                muscleGroups = muscleGroups, isDeleted = false
            )
            createExerciseTemplate(template)
                .onSuccess { loadTemplates() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun updateTemplate(id: String, name: String, description: String, muscleGroups: List<MuscleGroup>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val template = ExerciseTemplate(
                id = id.toIntOrNull() ?: 0, name = name,
                description = description, muscleGroups = muscleGroups, isDeleted = false
            )
            updateExerciseTemplate(id, template)
                .onSuccess {
                    _uiState.update { it.copy(isEditDialogOpen = false, selectedTemplate = null) }
                    loadTemplates()
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun deleteTemplate(id: String) {
        viewModelScope.launch {
            deleteExerciseTemplate(id)
                .onSuccess { loadTemplates() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun setFilter(muscleGroup: MuscleGroup?) {
        _uiState.update { it.copy(filterMuscleGroup = muscleGroup) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
