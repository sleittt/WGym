package com.example.data.local.entity.workout

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.local.entity.common.SyncMetadata
import kotlinx.datetime.Instant
import java.time.LocalDate

@Entity(
    tableName = "workouts",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["template_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["server_id"], unique = true),
        Index(value = ["template_id"]),
        Index(value = ["date"])
    ]
)
data class WorkoutEntity(
    @PrimaryKey val id: String,
    @Embedded val sync: SyncMetadata,
    @ColumnInfo(name = "template_id") val templateId: Int?,
    val startTime: Instant? = null,
    val endTime: Instant? = null,
    val note: String = "",
    val date: LocalDate
)
