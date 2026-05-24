package com.example.domain.usecase.meal

import com.example.domain.model.meal.FoodItem
import com.example.domain.repository.MealRepository
import javax.inject.Inject

class GetFoodItemByIdUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(foodItemId: String): FoodItem? = mealRepository.getFoodItemById(foodItemId)
}