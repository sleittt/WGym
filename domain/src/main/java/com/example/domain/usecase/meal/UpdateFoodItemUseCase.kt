package com.example.domain.usecase.meal

import com.example.domain.model.meal.FoodItem
import com.example.domain.repository.MealRepository
import javax.inject.Inject

class UpdateFoodItemUseCase @Inject constructor(
    private val mealRepository: MealRepository
){
    suspend operator fun  invoke(foodItemId: String, updatedFoodItem: FoodItem): Result<FoodItem>{
        val existing = mealRepository.getFoodItemById(foodItemId)
            ?: return Result.failure(IllegalArgumentException("Продукт не найден"))
        if (updatedFoodItem.name.isBlank()){
            return Result.failure(IllegalArgumentException("Название не может быть пустым"))
        }
        return try {
            val updated = mealRepository.updateFoodItem(foodItemId,updatedFoodItem)
            Result.success(updated)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}