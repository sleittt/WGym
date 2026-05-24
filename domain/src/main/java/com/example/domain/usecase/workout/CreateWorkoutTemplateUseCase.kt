package com.example.domain.usecase.workout

import com.example.domain.model.workout.WorkoutTemplate
import com.example.domain.repository.WorkoutRepository
import javax.inject.Inject

class CreateWorkoutTemplateUseCase @Inject constructor(
    private val repository: WorkoutRepository
){
    suspend operator fun invoke(template: WorkoutTemplate): Result<WorkoutTemplate>{
        if (template.name.isBlank()) return Result.failure(IllegalArgumentException("Имя не может быть пустым"))
        if (template.exercise.isEmpty()) return Result.failure(IllegalArgumentException("Шаблон не может быть пустым"))
        return try {
            val created = repository.createTemplate(template)
            Result.success(created)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}