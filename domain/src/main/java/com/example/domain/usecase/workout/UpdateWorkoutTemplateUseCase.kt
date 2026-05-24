package com.example.domain.usecase.workout

import com.example.domain.model.workout.WorkoutTemplate
import com.example.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateWorkoutTemplateUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke (id: String, updated: WorkoutTemplate): Result<WorkoutTemplate>{
        val existing = repository.getWorkoutTemplateById(id).first()
            ?: return Result.failure(IllegalArgumentException("Шаблона не существует"))
        if (existing.isDeleted) return Result.failure(IllegalStateException("Шаблон удален"))
        if (updated.name.isBlank()) return Result.failure(IllegalArgumentException("Имя не может быть пустым"))
        if (updated.exercise.isEmpty()) return Result.failure(IllegalArgumentException("Шаблон не может быть пустым"))
        return try {
            val result = repository.updateTemplate(id, updated)
            Result.success(updated)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}