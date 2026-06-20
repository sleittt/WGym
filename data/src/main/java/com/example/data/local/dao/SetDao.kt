package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.workout.SetEntity

@Dao
interface SetDao {

    @Query("SELECT * FROM sets WHERE exercise_id = :exerciseId")
    suspend fun getByExerciseId(exerciseId: Int): List<SetEntity>

    @Query("SELECT * FROM sets WHERE id = :id")
    suspend fun getById(id: Int): SetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: SetEntity): Long

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(sets: List<SetEntity>): List<Long>

    @Update
    suspend fun update(set: SetEntity)

    @Delete
    suspend fun delete(set: SetEntity)

    @Query("DELETE FROM sets WHERE exercise_id = :exerciseId")
    suspend fun deleteByExerciseId(exerciseId: Int)
}
