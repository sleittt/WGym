package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.meal.MealItemEntity

@Dao
interface MealItemDao {

    @Query("SELECT * FROM meal_items WHERE meal_id = :mealId")
    suspend fun getByMealId(mealId: String): List<MealItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MealItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MealItemEntity>)

    @Update
    suspend fun update(item: MealItemEntity)

    @Delete
    suspend fun delete(item: MealItemEntity)

    @Query("DELETE FROM meal_items WHERE meal_id = :mealId")
    suspend fun deleteByMealId(mealId: String)
}
