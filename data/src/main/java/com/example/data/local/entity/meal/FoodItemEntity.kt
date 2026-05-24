package com.example.data.local.entity.meal

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.local.entity.common.SyncMetadata

@Entity(
    tableName = "food_items",
    indices = [Index(value = ["server_id"], unique = true)]
)
data class FoodItemEntity(
    @PrimaryKey val id: String,
    @Embedded val sync: SyncMetadata,
    val name: String,
    val description: String = "",
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val fatsPer100g: Double,
    val carbsPer100g: Double,
    val servingDefaultGrams: Double = 100.0,
    val isDeleted: Boolean = false
)
