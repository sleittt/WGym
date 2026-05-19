package com.example.domain.model.workout

import kotlin.time.Duration

data class Set (
    val id: Int,
    val note: String = "",
    val rest: Duration,
    val load: Float, //вес
    val reps: Int,
    val type: SetType = SetType.NORMAL,
    val isCompleted: Boolean = false
)

enum class SetType (val displayName: String){
    NORMAL("Обычный"),
    FAILURE("До отказа"),
    WARMUP("Разминочный")
}
//TODO историю в отдельный usecase выносим