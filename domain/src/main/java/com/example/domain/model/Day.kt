package com.example.domain.model

import com.example.domain.model.meal.Meal
import com.example.domain.model.workout.Workout
import java.time.LocalDate

data class Day(
    val date: LocalDate,  // вместо id: Int
    val workoutSessions: List<Workout>,
    val meals: List<Meal>
)