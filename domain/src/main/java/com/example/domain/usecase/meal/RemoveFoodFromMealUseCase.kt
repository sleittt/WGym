package com.example.domain.usecase.meal

import com.example.domain.repository.MealRepository
import javax.inject.Inject

class RemoveFoodFromMealUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(mealId: String, foodItemId: String): Result<Unit>{
        return try {
            mealRepository.removeFoodFromMeal(mealId,foodItemId)
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}