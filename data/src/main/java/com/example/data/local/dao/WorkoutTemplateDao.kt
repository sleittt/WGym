package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.workout.WorkoutTemplateEntity
import com.example.data.local.entity.workout.WorkoutTemplateWithExercises

@Dao
interface WorkoutTemplateDao {

    @Query("SELECT * FROM workout_templates WHERE isDeleted = 0 ORDER BY useCount DESC")
    suspend fun getAll(): List<WorkoutTemplateEntity>

    @Query("SELECT * FROM workout_templates WHERE id = :id")
    suspend fun getById(id: Int): WorkoutTemplateEntity?

    @Transaction
    @Query("SELECT * FROM workout_templates WHERE id = :id")
    suspend fun getWithExercises(id: Int): WorkoutTemplateWithExercises?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: WorkoutTemplateEntity): Long

    @Update
    suspend fun update(template: WorkoutTemplateEntity)

    @Delete
    suspend fun delete(template: WorkoutTemplateEntity)

    @Query("UPDATE workout_templates SET isDeleted = 1, sync_status = 'PENDING_DELETE' WHERE id = :id")
    suspend fun softDelete(id: Int)

    @Query("SELECT * FROM workout_templates WHERE sync_status != 'SYNCED'")
    suspend fun getUnsynced(): List<WorkoutTemplateEntity>
}
