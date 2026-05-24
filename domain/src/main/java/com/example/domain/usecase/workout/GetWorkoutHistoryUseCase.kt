package com.example.domain.usecase.workout

import com.example.domain.model.workout.Workout
import com.example.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkoutHistoryUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<Workout>> = repository.observeWorkoutHistory()
}