package com.example.domain.model.meal

import kotlin.time.Instant

data class Meal(
    val id: String,
    val foodItem: FoodItem,
    val grams: Double,
    val timestamp: Instant
){
    val calories: Double get() = foodItem.caloriesPer100g * grams / 100
    val protein: Double get() = foodItem.proteinPer100g * grams / 100
    val carbs: Double get() = foodItem.carbsPer100g * grams / 100
    val fats: Double get() = foodItem.fatsPer100g * grams / 100
}