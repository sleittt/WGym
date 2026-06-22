package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.workout.ExerciseEntity
import com.example.data.local.entity.workout.ExerciseWithSets
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises WHERE workout_id = :workoutId AND isDeleted = 0 ORDER BY `order`")
    fun observeByWorkoutId(workoutId: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE template_id = :templateId AND isDeleted = 0 ORDER BY `order`")
    suspend fun getByTemplateId(templateId: Int): List<ExerciseEntity>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getById(id: Int): ExerciseEntity?

    @Transaction
    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getWithSets(id: Int): ExerciseWithSets?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: ExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>): List<Long>

    @Update
    suspend fun update(exercise: ExerciseEntity)

    @Delete
    suspend fun delete(exercise: ExerciseEntity)

    @Query("DELETE FROM exercises WHERE workout_id = :workoutId")
    suspend fun deleteByWorkoutId(workoutId: String)

    @Query("DELETE FROM exercises WHERE template_id = :templateId")
    suspend fun deleteByTemplateId(templateId: Int)

    // Soft delete
    @Query("UPDATE exercises SET isDeleted = 1, sync_status = 'PENDING_DELETE' WHERE id = :id")
    suspend fun softDelete(id: Int)

    @Query("UPDATE exercises SET isDeleted = 1, sync_status = 'PENDING_DELETE' WHERE template_id = :templateId")
    suspend fun softDeleteByTemplateId(templateId: Int)

    @Query("UPDATE exercises SET isDeleted = 1, sync_status = 'PENDING_DELETE' WHERE exercise_template_id = :exerciseTemplateId")
    suspend fun softDeleteByExerciseTemplateId(exerciseTemplateId: Int)

    @Query("SELECT * FROM exercises WHERE sync_status != 'SYNCED'")
    suspend fun getUnsynced(): List<ExerciseEntity>
}