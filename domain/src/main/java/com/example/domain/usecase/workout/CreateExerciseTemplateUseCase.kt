package com.example.domain.usecase.workout

import com.example.domain.model.workout.ExerciseTemplate
import com.example.domain.repository.WorkoutRepository
import javax.inject.Inject

class CreateExerciseTemplateUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(template: ExerciseTemplate): Result<ExerciseTemplate>{
        if (template.name.isBlank()) return Result.failure(IllegalArgumentException("Название не моэжет быть пустым"))
        return try {
            val created = repository.createExerciseTemplate(template)
            Result.success(created)
        } catch (e : Exception) {
            Result.failure(e)
        }
    }
}