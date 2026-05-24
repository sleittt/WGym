package com.example.domain.usecase.workout

import com.example.domain.model.workout.Workout
import com.example.domain.repository.WorkoutRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject
import kotlin.time.Clock

class CreateWorkoutUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(workout: Workout): Result<Workout>{
        if (workout.date > Clock.System.todayIn(TimeZone.currentSystemDefault())) return Result.failure(
            IllegalArgumentException("Дата тренировки не может быть в будущем"))
        if (workout.template!=null){
            repository.incrementTemplateUseCount(workout.template)
        }
        return try {
            val logged = repository.createWorkout(workout)
            Result.success(logged)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}