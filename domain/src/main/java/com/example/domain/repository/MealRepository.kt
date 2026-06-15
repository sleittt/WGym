package com.example.domain.repository

import com.example.domain.model.meal.FoodItem
import com.example.domain.model.meal.Meal
import com.example.domain.model.meal.MealType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate


interface MealRepository {
    //meal
    fun observeMealsByDate(date: LocalDate): Flow<List<Meal>>
    suspend fun addFoodToMeal(date: LocalDate, mealType: MealType, foodItemId: String, amountGrams: Float): Meal
    suspend fun removeFoodFromMeal(mealId: String, foodItemId: String) : Meal

    //food item
    fun observeAllFoodItems(): Flow<List<FoodItem>>
    suspend fun getFoodItemById(foodItemId: String): FoodItem?
    suspend fun getActiveFoodItemById(foodItemId: String): FoodItem?
    suspend fun addFoodItem(foodItem: FoodItem) : FoodItem
    suspend fun updateFoodItem(foodItemId: String, updatedFoodItem: FoodItem): FoodItem
    suspend fun deleteFoodItem(foodItemId: String)
    suspend fun isFoodItemUsed(foodItemId: String): Boolean
}