package com.example.domain.model.workout

data class WorkoutTemplate (
    val id: Int,
    val name: String,
    val useCount: Int,
    val isPinned: Boolean = false,
    val exercise: List<Exercise>,
    val isDeleted: Boolean
)