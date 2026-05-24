package com.example.domain.usecase.workout

import com.example.domain.repository.WorkoutRepository
import javax.inject.Inject

class DeleteExerciseTemplateUseCase @Inject constructor(
    private val repository: WorkoutRepository
){
    suspend operator fun invoke(id: String): Result<Unit>{
        val existing = repository.getExerciseTemplateById(id)
            ?: return Result.failure(IllegalArgumentException("Шаблон не найден"))
        if (existing.isDeleted) return Result.failure(IllegalStateException("Шаблон уже удален"))
        return try {
            repository.deleteExerciseTemplate(id)
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}