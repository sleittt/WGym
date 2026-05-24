package com.example.data.local.entity.workout

import androidx.room.Embedded
import androidx.room.Relation

data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        entity = ExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "workout_id"
    )
    val exercises: List<ExerciseWithSets>
)
