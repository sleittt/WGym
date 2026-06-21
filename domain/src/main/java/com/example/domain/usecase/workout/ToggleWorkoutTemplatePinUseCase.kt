package com.example.domain.usecase.workout

import com.example.domain.repository.WorkoutRepository
import javax.inject.Inject

class ToggleWorkoutTemplatePinUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(id: String, isPinned: Boolean) {
        repository.setTemplatePinned(id, isPinned)
    }
}