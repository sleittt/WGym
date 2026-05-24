package com.example.domain.usecase.workout

import com.example.domain.model.workout.ExerciseTemplate
import com.example.domain.repository.WorkoutRepository
import javax.inject.Inject

class UpdateExerciseTemplateUseCase @Inject constructor(
    private val repo: WorkoutRepository
) {
    suspend operator fun invoke(id: String, updated: ExerciseTemplate): Result<ExerciseTemplate>{
        val existing = repo.getExerciseTemplateById(id)
            ?: return Result.failure(IllegalArgumentException("Шаблон не найден"))
        if (updated.name.isBlank()) return Result.failure(IllegalArgumentException("азвание не может быть пустым"))
        if (existing.isDeleted) return Result.failure(IllegalStateException("Нельзя редактировать удаленный шаблон"))
        return try {
            val result = repo.updateExerciseTemplate(id,updated)
            Result.success(result)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}