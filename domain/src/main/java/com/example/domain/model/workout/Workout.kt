package com.example.domain.model.workout

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

data class Workout(
    val id: String,
    val template: WorkoutTemplate,
    val startTime: Instant? = null, // только для выполненной
    val endTime: Instant?,
    val exercises: List<Exercise>,
    val note: String = "",
    val date: LocalDate
)
