package com.example.domain.model.workout

import java.sql.Time
import kotlin.time.Duration
import kotlin.time.Instant

data class Workout(
    val id: String,
    val isTemplate: Boolean, // true = шаблон, false = выполненная тренировка    val startTime: Instant,
    val startTime: Instant? = null, // только для выполненной
    val endTime: Instant?,
    val exercises: List<Exercise>,
    val note: String = ""
)
