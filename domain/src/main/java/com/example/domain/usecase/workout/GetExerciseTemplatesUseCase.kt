package com.example.domain.usecase.workout

import com.example.domain.model.workout.ExerciseTemplate
import com.example.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExerciseTemplatesUseCase @Inject constructor(
    private val repo: WorkoutRepository
) {
    operator fun invoke(): Flow<List<ExerciseTemplate>> = repo.observeExerciseTemplates()
}