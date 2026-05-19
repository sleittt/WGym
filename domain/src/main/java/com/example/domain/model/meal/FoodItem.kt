package com.example.domain.model.meal

data class FoodItem(
    val id: String,
    val name: String,
    val description: String = "",
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val fatsPer100g: Double,
    val carbsPer100g: Double,
    val servingDefaultGrams: Double = 100.0,
    val photoUrl: String? = null
)