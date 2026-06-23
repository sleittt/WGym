package com.example.domain.usecase.workout

import com.example.domain.model.workout.Workout
import com.example.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetWorkoutsInPeriodUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<List<Workout>> {
        return repository.observeWorkoutHistory().map { workouts ->
            workouts.filter { workout ->
                !workout.date.isBefore(startDate) && !workout.date.isAfter(endDate)
            }
        }
    }
}
