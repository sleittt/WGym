package com.example.domain.usecase.meal

import com.example.domain.repository.MealRepository
import javax.inject.Inject

class DeleteFoodItemUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(foodItemId: String): Result<Unit>{
        val existing = mealRepository.getFoodItemById(foodItemId)
            ?: return Result.failure(IllegalArgumentException("Продукт не найден"))
        return try {
            mealRepository.updateFoodItem(
                foodItemId,
                existing.copy(isDeleted = true)
            )
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}