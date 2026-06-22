package com.example.domain.model.workout

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class WorkoutTemplate (
    val id: Int,
    val name: String,
    val useCount: Int,
    val isPinned: Boolean = false,
    val exercise: List<Exercise>,
    val isDeleted: Boolean,
    val defaultRestTime: Duration = 90.seconds
)