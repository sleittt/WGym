package com.example.domain.model

import com.example.domain.model.meal.Meal
import com.example.domain.model.workout.Workout

data class Day(
    val id: Int,
    val workoutSessions: List<Workout>,
    val meals: List<Meal>
)
