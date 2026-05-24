package com.example.data.local.entity.meal

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MealWithItems(
    @Embedded val meal: MealEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "meal_id"
    )
    val items: List<MealItemEntity>
)
