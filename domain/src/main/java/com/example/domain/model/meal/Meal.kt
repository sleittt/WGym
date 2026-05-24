package com.example.domain.model.meal

import kotlinx.datetime.LocalDate

data class Meal(
    val id: String,
    val items: List<Item>,
    val type: MealType,
    val date: LocalDate
){
    data class Item(
        val foodItemId: String,
        val amountGrams: Float
    )
}

enum class MealType {BREAKFAST, LUNCH, DINNER, SNACK}