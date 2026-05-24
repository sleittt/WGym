package com.example.data.local.entity.meal

import androidx.room.Embedded
import androidx.room.Relation

data class MealWithItemsAndFood(
    @Embedded val meal: MealEntity,
    @Relation(
        entity = MealItemEntity::class,
        parentColumn = "id",
        entityColumn = "meal_id"
    )
    val items: List<MealItemWithFood>
)
