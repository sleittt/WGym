package com.example.domain.usecase.meal

import com.example.domain.model.meal.Meal
import com.example.domain.repository.MealRepository
import java.time.LocalDate
import javax.inject.Inject

class GetMealsInPeriodUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(startDate: LocalDate, endDate: LocalDate): List<Meal> {
        return mealRepository.getMealsInPeriod(startDate, endDate)
    }
}
