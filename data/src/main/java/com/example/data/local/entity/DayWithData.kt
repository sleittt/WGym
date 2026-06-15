package com.example.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.local.entity.meal.MealEntity
import com.example.data.local.entity.workout.WorkoutEntity
import java.time.LocalDate

data class DayWithData(
    val date: LocalDate,
    @Relation(
        parentColumn = "date",
        entityColumn = "date"
    )
    val workouts: List<WorkoutEntity>,
    @Relation(
        parentColumn = "date",
        entityColumn = "date"
    )
    val meals: List<MealEntity>
)
