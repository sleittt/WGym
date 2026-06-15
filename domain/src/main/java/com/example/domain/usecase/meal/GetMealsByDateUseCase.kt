package com.example.domain.usecase.meal

import com.example.domain.model.meal.Meal
import com.example.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetMealsByDateUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<Meal>> = mealRepository.observeMealsByDate(date)
}