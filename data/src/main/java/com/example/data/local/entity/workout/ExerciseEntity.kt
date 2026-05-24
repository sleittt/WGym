package com.example.data.local.entity.workout

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.local.entity.common.SyncMetadata

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkoutTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["template_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_template_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["server_id"], unique = true),
        Index(value = ["workout_id"]),
        Index(value = ["template_id"]),
        Index(value = ["exercise_template_id"])
    ]
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Embedded val sync: SyncMetadata,
    @ColumnInfo(name = "workout_id") val workoutId: String?,
    @ColumnInfo(name = "template_id") val templateId: Int?,
    @ColumnInfo(name = "exercise_template_id") val exerciseTemplateId: Int,
    val note: String = "",
    val order: Int
)
