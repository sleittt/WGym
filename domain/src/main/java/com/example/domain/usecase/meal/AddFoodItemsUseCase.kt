package com.example.domain.usecase.meal

import com.example.domain.model.meal.FoodItem
import com.example.domain.repository.MealRepository
import javax.inject.Inject

class AddFoodItemsUseCase @Inject constructor(
    private val mealRepository: MealRepository
){
    suspend operator fun invoke(foodItem: FoodItem): Result<FoodItem>{
        if (foodItem.name.isBlank()){
            return Result.failure(IllegalArgumentException("Назване не может быть пустым"))
        }
        if (foodItem.caloriesPer100g < 0 || foodItem.carbsPer100g !in 0.0..100.0|| foodItem.fatsPer100g !in 0.0..100.0 || foodItem.carbsPer100g !in 0.0..100.0){
            return Result.failure(IllegalArgumentException("Значение КБЖУ некорректно"))
        }
        return try {
            val savedFoodItem = mealRepository.addFoodItem(foodItem)
            Result.success(savedFoodItem)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}