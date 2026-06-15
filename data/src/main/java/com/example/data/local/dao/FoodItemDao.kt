package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.meal.FoodItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {

    @Query("SELECT * FROM food_items WHERE isDeleted = 0 ORDER BY name")
    fun observeAll(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getById(id: String): FoodItemEntity?

    @Query("SELECT * FROM food_items WHERE id = :id AND isDeleted = 0")
    suspend fun getActiveById(id: String): FoodItemEntity?

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' AND isDeleted = 0")
    suspend fun search(query: String): List<FoodItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FoodItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FoodItemEntity>)

    @Update
    suspend fun update(item: FoodItemEntity)

    @Delete
    suspend fun delete(item: FoodItemEntity)

    @Query("UPDATE food_items SET isDeleted = 1, sync_status = 'PENDING_DELETE' WHERE id = :id")
    suspend fun softDelete(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM meal_items WHERE food_item_id = :foodItemId LIMIT 1)")
    suspend fun isUsed(foodItemId: String): Boolean

    @Query("SELECT * FROM food_items WHERE sync_status != 'SYNCED'")
    suspend fun getUnsynced(): List<FoodItemEntity>
}
