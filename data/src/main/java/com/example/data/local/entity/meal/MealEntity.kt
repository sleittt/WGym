package com.example.data.local.entity.meal

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.local.entity.common.SyncMetadata
import com.example.domain.model.meal.MealType
import java.time.LocalDate

@Entity(
    tableName = "meals",
    indices = [
        Index(value = ["server_id"], unique = true),
        Index(value = ["date"])
    ]
)
data class MealEntity(
    @PrimaryKey val id: String,
    @Embedded val sync: SyncMetadata,
    val type: MealType,
    val date: LocalDate
)
