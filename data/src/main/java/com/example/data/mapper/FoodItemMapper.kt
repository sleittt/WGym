package com.example.data.mapper

import com.example.data.local.entity.common.SyncMetadata
import com.example.data.local.entity.meal.FoodItemEntity
import com.example.domain.model.meal.FoodItem

object FoodItemMapper {

    fun toDomain(entity: FoodItemEntity): FoodItem = FoodItem(
        id = entity.id,
        name = entity.name,
        description = entity.description,
        caloriesPer100g = entity.caloriesPer100g,
        proteinPer100g = entity.proteinPer100g,
        fatsPer100g = entity.fatsPer100g,
        carbsPer100g = entity.carbsPer100g,
        servingDefaultGrams = entity.servingDefaultGrams,
        isDeleted = entity.isDeleted
    )

    fun toEntity(domain: FoodItem, sync: SyncMetadata): FoodItemEntity = FoodItemEntity(
        id = domain.id,
        sync = sync,
        name = domain.name,
        description = domain.description,
        caloriesPer100g = domain.caloriesPer100g,
        proteinPer100g = domain.proteinPer100g,
        fatsPer100g = domain.fatsPer100g,
        carbsPer100g = domain.carbsPer100g,
        servingDefaultGrams = domain.servingDefaultGrams,
        isDeleted = domain.isDeleted
    )
}
