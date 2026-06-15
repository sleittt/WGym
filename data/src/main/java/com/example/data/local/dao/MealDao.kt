package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.meal.MealEntity
import com.example.data.local.entity.meal.MealItemEntity
import com.example.data.local.entity.meal.MealWithItems
import com.example.data.local.entity.meal.MealWithItemsAndFood
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MealDao {

    @Query("SELECT * FROM meals WHERE date = :date ORDER BY id")
    fun observeByDate(date: LocalDate): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getById(id: String): MealEntity?

    @Transaction
    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getWithItems(id: String): MealWithItems?

    @Transaction
    @Query("SELECT * FROM meals WHERE date = :date ORDER BY id")
    fun observeWithItemsAndFoodByDate(date: LocalDate): Flow<List<MealWithItemsAndFood>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: MealEntity)

    @Update
    suspend fun update(meal: MealEntity)

    @Delete
    suspend fun delete(meal: MealEntity)

    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM meals WHERE sync_status != 'SYNCED'")
    suspend fun getUnsynced(): List<MealEntity>
}
