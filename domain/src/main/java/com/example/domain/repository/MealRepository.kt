package com.example.domain.repository

import com.example.domain.model.meal.FoodItem
import com.example.domain.model.meal.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface MealRepository {
    //meal
    fun observeAllMeals(): Flow<List<Meal>>
    fun observeMealsByDate(date: LocalDate): Flow<List<Meal>>
    suspend fun getMealById(mealId: String): Meal
    suspend fun saveMeal(meal: Meal) : Meal
    suspend fun updateMeal(meal: Meal): Meal
    suspend fun deleteMeal(mealId: String)
    //food item
    fun observeAllFoodItems(): Flow<List<FoodItem>>
    suspend fun getFoodItemById(foodItemId: String): FoodItem?
    suspend fun addFoodItem(foodItem: FoodItem) : FoodItem
    suspend fun updateFoodItem(foodItem: FoodItem): FoodItem
    suspend fun deleteFoodItem(foodItemId: String)
}