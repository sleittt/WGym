package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.workout.ExerciseTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseTemplateDao {

    @Query("SELECT * FROM exercise_templates WHERE isDeleted = 0 ORDER BY name")
    fun observeAll(): Flow<List<ExerciseTemplateEntity>>

    @Query("SELECT * FROM exercise_templates WHERE isDeleted = 0 ORDER BY name")
    suspend fun getAll(): List<ExerciseTemplateEntity>

    @Query("SELECT * FROM exercise_templates WHERE id = :id")
    suspend fun getById(id: Int): ExerciseTemplateEntity?

    @Query("SELECT * FROM exercise_templates WHERE muscleGroups LIKE '%' || :group || '%' AND isDeleted = 0")
    suspend fun getByMuscleGroup(group: String): List<ExerciseTemplateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: ExerciseTemplateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<ExerciseTemplateEntity>)

    @Update
    suspend fun update(template: ExerciseTemplateEntity)

    @Delete
    suspend fun delete(template: ExerciseTemplateEntity)

    @Query("UPDATE exercise_templates SET isDeleted = 1, sync_status = 'PENDING_DELETE' WHERE id = :id")
    suspend fun softDelete(id: Int)

    @Query("SELECT * FROM exercise_templates WHERE sync_status != 'SYNCED'")
    suspend fun getUnsynced(): List<ExerciseTemplateEntity>
}
