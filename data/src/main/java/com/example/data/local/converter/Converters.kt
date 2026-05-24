package com.example.data.local.converter

import androidx.room.TypeConverter
import com.example.data.local.entity.common.SyncStatus
import com.example.domain.model.meal.MealType
import com.example.domain.model.workout.MuscleGroup
import com.example.domain.model.workout.SetType
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Converters {

    private val json = Json { ignoreUnknownKeys = true }

    // --- Instant ---
    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilliseconds()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.fromEpochMilliseconds(it) }

    // --- LocalDate ---
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    // --- Duration ---
    @TypeConverter
    fun fromDuration(value: Duration?): Long? = value?.inWholeMilliseconds

    @TypeConverter
    fun toDuration(value: Long?): Duration? = value?.toDuration(DurationUnit.MILLISECONDS)
    // --- SyncStatus ---
    @TypeConverter
    fun fromSyncStatus(value: SyncStatus?): String? = value?.name

    @TypeConverter
    fun toSyncStatus(value: String?): SyncStatus? = value?.let { SyncStatus.valueOf(it) }

    // --- MealType ---
    @TypeConverter
    fun fromMealType(value: MealType?): String? = value?.name

    @TypeConverter
    fun toMealType(value: String?): MealType? = value?.let { MealType.valueOf(it) }

    // --- SetType ---
    @TypeConverter
    fun fromSetType(value: SetType?): String? = value?.name

    @TypeConverter
    fun toSetType(value: String?): SetType? = value?.let { SetType.valueOf(it) }

    // --- MuscleGroup list ---
    @TypeConverter
    fun fromMuscleGroupList(value: List<MuscleGroup>?): String =
        json.encodeToString(value ?: emptyList())

    @TypeConverter
    fun toMuscleGroupList(value: String?): List<MuscleGroup> =
        value?.let { json.decodeFromString(it) } ?: emptyList()

    // --- String list (for image urls etc) ---
    @TypeConverter
    fun fromStringList(value: List<String>?): String =
        json.encodeToString(value ?: emptyList())

    @TypeConverter
    fun toStringList(value: String?): List<String> =
        value?.let { json.decodeFromString(it) } ?: emptyList()

    // --- PostContent JSON ---
}
