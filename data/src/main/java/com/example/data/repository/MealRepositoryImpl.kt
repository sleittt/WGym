package com.example.data.repository

import com.example.data.local.dao.FoodItemDao
import com.example.data.local.dao.MealDao
import com.example.data.local.dao.MealItemDao
import com.example.data.local.entity.meal.MealEntity
import com.example.data.local.entity.meal.MealItemEntity
import com.example.data.mapper.FoodItemMapper
import com.example.data.mapper.MealMapper
import com.example.data.mapper.SyncMetadataMapper
import com.example.domain.model.meal.FoodItem
import com.example.domain.model.meal.Meal
import com.example.domain.model.meal.MealType
import com.example.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val mealItemDao: MealItemDao,
    private val foodItemDao: FoodItemDao
) : MealRepository {

    override fun observeMealsByDate(date: LocalDate): Flow<List<Meal>> {
        return mealDao.observeWithItemsAndFoodByDate(date).map { list ->
            list.map { mealWithItems ->
                MealMapper.toDomain(mealWithItems.meal, mealWithItems.items.map { it.mealItem })
            }
        }
    }

    override suspend fun addFoodToMeal(date: LocalDate, mealType: MealType, foodItemId: String, amountGrams: Float): Meal {
        val meals = mealDao.observeByDate(date).first()
        val existingMeal = meals.firstOrNull { it.type == mealType }

        val meal = if (existingMeal != null) {
            existingMeal
        } else {
            val newMeal = MealEntity(
                id = UUID.randomUUID().toString(),
                sync = SyncMetadataMapper.createPending(),
                type = mealType,
                date = date
            )
            mealDao.insert(newMeal)
            newMeal
        }

        val existingItem = mealItemDao.getByMealId(meal.id).find { it.foodItemId == foodItemId }
        if (existingItem != null) {
            mealItemDao.update(existingItem.copy(amountGrams = existingItem.amountGrams + amountGrams))
        } else {
            mealItemDao.insert(
                MealItemEntity(
                    mealId = meal.id,
                    foodItemId = foodItemId,
                    amountGrams = amountGrams
                )
            )
        }

        return MealMapper.toDomain(meal, mealItemDao.getByMealId(meal.id))
    }

    override suspend fun removeFoodFromMeal(mealId: String, foodItemId: String): Meal {
        mealItemDao.deleteByMealAndFood(mealId, foodItemId)
        val meal = mealDao.getById(mealId) ?: throw IllegalStateException("Meal not found")
        val items = mealItemDao.getByMealId(mealId)
        return MealMapper.toDomain(meal, items)
    }

    override fun observeAllFoodItems(): Flow<List<FoodItem>> {
        return foodItemDao.observeAll().map { list -> list.map { FoodItemMapper.toDomain(it) } }
    }

    override suspend fun getFoodItemById(foodItemId: String): FoodItem? {
        return foodItemDao.getById(foodItemId)?.let { FoodItemMapper.toDomain(it) }
    }

    override suspend fun getActiveFoodItemById(foodItemId: String): FoodItem? {
        return foodItemDao.getActiveById(foodItemId)?.let { FoodItemMapper.toDomain(it) }
    }

    override suspend fun addFoodItem(foodItem: FoodItem): FoodItem {
        val entity = FoodItemMapper.toEntity(foodItem, SyncMetadataMapper.createPending())
        foodItemDao.insert(entity)
        return foodItem
    }

    override suspend fun updateFoodItem(foodItemId: String, updatedFoodItem: FoodItem): FoodItem {
        val existing = foodItemDao.getById(foodItemId) ?: throw IllegalStateException("FoodItem not found")
        val sync = SyncMetadataMapper.updatePending(existing.sync)
        val entity = FoodItemMapper.toEntity(updatedFoodItem.copy(id = foodItemId), sync)
        foodItemDao.update(entity)
        return updatedFoodItem.copy(id = foodItemId)
    }

    override suspend fun deleteFoodItem(foodItemId: String) {
        foodItemDao.softDelete(foodItemId)
    }

    override suspend fun isFoodItemUsed(foodItemId: String): Boolean {
        return foodItemDao.isUsed(foodItemId)
    }
    override fun observeMealsInPeriod(startDate: LocalDate, endDate: LocalDate): Flow<List<Meal>> {
        return mealDao.observeWithItemsAndFoodInPeriod(startDate, endDate).map { list ->
            list.map { mealWithItems ->
                MealMapper.toDomain(mealWithItems.meal, mealWithItems.items.map { it.mealItem })
            }
        }
    }
    override suspend fun getMealsInPeriod(startDate: LocalDate, endDate: LocalDate): List<Meal> {
        return mealDao.getWithItemsAndFoodInPeriod(startDate, endDate).map { mealWithItems ->
            MealMapper.toDomain(mealWithItems.meal, mealWithItems.items.map { it.mealItem })
        }
    }
}
