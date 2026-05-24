package com.example.data.local.entity.meal

import androidx.room.Embedded
import androidx.room.Relation

data class MealItemWithFood(
    @Embedded val mealItem: MealItemEntity,
    @Relation(
        parentColumn = "food_item_id",
        entityColumn = "id"
    )
    val food: FoodItemEntity?
)
