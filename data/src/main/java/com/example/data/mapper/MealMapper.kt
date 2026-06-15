package com.example.data.mapper

import com.example.data.local.entity.common.SyncMetadata
import com.example.data.local.entity.meal.MealEntity
import com.example.data.local.entity.meal.MealItemEntity
import com.example.domain.model.meal.Meal

object MealMapper {

    fun toDomain(entity: MealEntity, items: List<MealItemEntity>): Meal = Meal(
        id = entity.id,
        items = items.map { Meal.Item(it.foodItemId, it.amountGrams) },
        type = entity.type,
        date = entity.date
    )

    fun toEntity(domain: Meal, sync: SyncMetadata): Pair<MealEntity, List<MealItemEntity>> {
        val mealEntity = MealEntity(
            id = domain.id,
            sync = sync,
            type = domain.type,
            date = domain.date
        )
        val itemEntities = domain.items.map {
            MealItemEntity(mealId = domain.id, foodItemId = it.foodItemId, amountGrams = it.amountGrams)
        }
        return mealEntity to itemEntities
    }
}
