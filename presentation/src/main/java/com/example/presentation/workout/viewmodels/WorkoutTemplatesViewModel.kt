package com.example.presentation.workout.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.workout.Exercise
import com.example.domain.model.workout.WorkoutTemplate
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
class WorkoutTemplatesViewModel @Inject constructor(
    private val getWorkoutTemplates: GetWorkoutTemplateUseCase,
    private val getWorkoutTemplateById: GetWorkoutTemplateByIdUseCase,
    private val createWorkoutTemplate: CreateWorkoutTemplateUseCase,
    private val updateWorkoutTemplate: UpdateWorkoutTemplateUseCase,
    private val deleteWorkoutTemplate: DeleteWorkoutTemplateUseCase
) : ViewModel() {

    data class UiState(
        val templates: List<WorkoutTemplate> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedTemplate: WorkoutTemplate? = null,
        val isEditDialogOpen: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        getWorkoutTemplates()
            .onEach { templates ->
                _uiState.update { it.copy(templates = templates, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun selectTemplate(template: WorkoutTemplate?) {
        _uiState.update { it.copy(selectedTemplate = template, isEditDialogOpen = template != null) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(isEditDialogOpen = false, selectedTemplate = null) }
    }

    fun createTemplate(name: String, exercises: List<Exercise>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val template = WorkoutTemplate(
                id = 0, name = name, useCount = 0, exercise = exercises, isDeleted = false
            )
            createWorkoutTemplate(template)
                .onSuccess { loadTemplates() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun updateTemplate(id: String, name: String, exercises: List<Exercise>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val template = WorkoutTemplate(
                id = id.toIntOrNull() ?: 0, name = name,
                useCount = _uiState.value.selectedTemplate?.useCount ?: 0,
                exercise = exercises, isDeleted = false
            )
            updateWorkoutTemplate(id, template)
                .onSuccess {
                    _uiState.update { it.copy(isEditDialogOpen = false, selectedTemplate = null) }
                    loadTemplates()
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun deleteTemplate(id: String) {
        viewModelScope.launch {
            deleteWorkoutTemplate(id)
                .onSuccess { loadTemplates() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
