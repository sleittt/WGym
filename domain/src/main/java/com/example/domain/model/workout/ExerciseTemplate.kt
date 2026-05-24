package com.example.domain.model.workout

data class ExerciseTemplate (
    val id:Int,
    val name: String,
    val description: String,
    val muscleGroups: List<MuscleGroup>,
    val isDeleted: Boolean
)