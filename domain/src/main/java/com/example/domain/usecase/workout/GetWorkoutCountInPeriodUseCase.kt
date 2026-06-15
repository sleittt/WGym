package com.example.domain.usecase.workout

import com.example.domain.repository.WorkoutRepository
import java.time.LocalDate
import javax.inject.Inject

class GetWorkoutCountInPeriodUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(startDate: LocalDate, endDate: LocalDate): Result<Int>{
        if (endDate>startDate) return Result.failure(IllegalArgumentException("Неккоректный период"))
        return try {
            val count = repository.getWorkoutCountInPeriod(startDate,endDate)
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}