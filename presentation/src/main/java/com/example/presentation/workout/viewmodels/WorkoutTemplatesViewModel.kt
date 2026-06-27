package com.example.presentation.workout.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.manager.WorkoutManager
import com.example.domain.model.workout.WorkoutTemplate
import com.example.domain.usecase.workout.DeleteWorkoutTemplateUseCase
import com.example.domain.usecase.workout.GetWorkoutTemplateUseCase
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
    private val deleteWorkoutTemplate: DeleteWorkoutTemplateUseCase,
    private val workoutManager: WorkoutManager
) : ViewModel() {

    data class UiState(
        val templates: List<WorkoutTemplate> = emptyList(),
        val pinnedTemplates: List<WorkoutTemplate> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        getWorkoutTemplates()
            .onEach { templates ->
                val cached = workoutManager.templateCache
                val merged = templates.map { cached[it.id] ?: it }
                val pinned = merged.filter { it.isPinned }.take(2)
                _uiState.update { it.copy(
                    templates = merged,
                    pinnedTemplates = pinned,
                    isLoading = false
                ) }
            }
            .launchIn(viewModelScope)
    }

    fun deleteTemplate(id: String) {
        viewModelScope.launch {
            deleteWorkoutTemplate(id)
                .onSuccess { loadTemplates() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun duplicateTemplate(id: String) {
        // TODO: get by id, create copy with name + " (копия)", id=0
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
