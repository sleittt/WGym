package com.example.domain.model.workout

data class WorkoutTemplate (
    val id: Int,
    val name: String,
    val useCount: Int,
    val exercise: List<Exercise>,
)