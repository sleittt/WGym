package com.example.domain.usecase.workout

import com.example.domain.model.workout.WorkoutTemplate
import com.example.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkoutTemplateByIdUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    operator fun invoke(id: String): Flow<WorkoutTemplate?> = repository.getWorkoutTemplateById(id)
}