package com.example.data.local.entity.workout

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.local.entity.common.SyncMetadata
import com.example.domain.model.workout.MuscleGroup

@Entity(
    tableName = "exercise_templates",
    indices = [Index(value = ["server_id"], unique = true)]
)
data class ExerciseTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Embedded val sync: SyncMetadata,
    val name: String,
    val description: String,
    val muscleGroups: List<MuscleGroup>, // TypeConverter
    val isDeleted: Boolean = false
)
