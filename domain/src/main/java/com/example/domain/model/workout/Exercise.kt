package com.example.domain.model.workout

data class Exercise (
    val id:Int,
    val template: ExerciseTemplate,
    val note: String = "",
    val sets: List<Set>,
    val order: Int
)