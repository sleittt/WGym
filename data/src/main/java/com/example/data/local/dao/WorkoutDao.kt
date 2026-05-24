package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.workout.WorkoutEntity
import com.example.data.local.entity.workout.WorkoutWithExercises
import kotlinx.datetime.LocalDate

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workouts WHERE date = :date ORDER BY id")
    suspend fun getByDate(date: LocalDate): List<WorkoutEntity>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getById(id: String): WorkoutEntity?

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWithExercises(id: String): WorkoutWithExercises?

    @Transaction
    @Query("SELECT * FROM workouts WHERE date = :date ORDER BY id")
    suspend fun getWithExercisesByDate(date: LocalDate): List<WorkoutWithExercises>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: WorkoutEntity)

    @Update
    suspend fun update(workout: WorkoutEntity)

    @Delete
    suspend fun delete(workout: WorkoutEntity)

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM workouts WHERE sync_status != 'SYNCED'")
    suspend fun getUnsynced(): List<WorkoutEntity>
}
