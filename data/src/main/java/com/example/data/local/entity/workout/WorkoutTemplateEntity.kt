package com.example.data.local.entity.workout

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.local.entity.common.SyncMetadata

@Entity(
    tableName = "workout_templates",
    indices = [Index(value = ["server_id"], unique = true)]
)
data class WorkoutTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Embedded val sync: SyncMetadata,
    val name: String,
    val useCount: Int = 0,
    val isPinned: Boolean = false,
    val isDeleted: Boolean = false,
    val defaultRestTimeSeconds: Long = 90
)