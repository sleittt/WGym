package com.example.domain.usecase.workout

import com.example.domain.model.workout.ExerciseTemplate
import com.example.domain.repository.WorkoutRepository
import javax.inject.Inject

class GetExerciseTemplateByIdUseCase @Inject constructor(
    private val repo: WorkoutRepository
) {
    operator fun invoke(id: String): ExerciseTemplate? = repo.getExerciseTemplateById(id)
}