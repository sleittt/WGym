package com.example.domain.usecase.meal

import com.example.domain.model.meal.MealType
import com.example.domain.repository.MealRepository
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class AddFoodToMealUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(
        date: LocalDate,
        mealType: MealType,
        foodItemId: String,
        amountGrams: Float
    ) : Result<Unit>{
        if (amountGrams<=0) return Result.failure(IllegalArgumentException("Количество должно быть положительным"))
        val foodItem = mealRepository.getActiveFoodItemById(foodItemId)
            ?: return Result.failure(IllegalArgumentException("Продукт не найден"))
        return try {
            mealRepository.addFoodToMeal(date,mealType,foodItemId,amountGrams)
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}