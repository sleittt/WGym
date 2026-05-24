package com.example.data.local.entity.workout

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.local.entity.common.SyncMetadata
import com.example.domain.model.workout.SetType
import kotlin.time.Duration

@Entity(
    tableName = "sets",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["server_id"], unique = true),
        Index(value = ["exercise_id"])
    ]
)
data class SetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @Embedded val sync: SyncMetadata,
    @ColumnInfo(name = "exercise_id") val exerciseId: Int,
    val note: String = "",
    val rest: Duration, // TypeConverter
    val load: Float,
    val reps: Int,
    val type: SetType = SetType.NORMAL,
    val isCompleted: Boolean = false
)
