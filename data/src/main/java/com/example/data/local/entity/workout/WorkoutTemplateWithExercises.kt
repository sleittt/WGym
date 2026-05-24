package com.example.data.local.entity.workout

import androidx.room.Embedded
import androidx.room.Relation

data class WorkoutTemplateWithExercises(
    @Embedded val template: WorkoutTemplateEntity,
    @Relation(
        entity = ExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "template_id"
    )
    val exercises: List<ExerciseWithSets>
)
