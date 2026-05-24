package com.example.domain.usecase.meal

import com.example.domain.model.meal.FoodItem
import com.example.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFoodItemsUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    operator fun invoke(): Flow<List<FoodItem>> = mealRepository.observeAllFoodItems()
}