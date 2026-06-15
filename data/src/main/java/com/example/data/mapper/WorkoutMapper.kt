package com.example.data.mapper

import com.example.data.local.entity.common.SyncMetadata
import com.example.data.local.entity.workout.WorkoutEntity
import com.example.domain.model.workout.Exercise
import com.example.domain.model.workout.Workout
import com.example.domain.model.workout.WorkoutTemplate

object WorkoutMapper {

    fun toDomain(entity: WorkoutEntity, template: WorkoutTemplate, exercises: List<Exercise>): Workout = Workout(
        id = entity.id,
        template = template,
        startTime = entity.startTime,
        endTime = entity.endTime,
        exercises = exercises,
        note = entity.note,
        date = entity.date
    )

    fun toEntity(domain: Workout, templateId: Int?, sync: SyncMetadata): WorkoutEntity = WorkoutEntity(
        id = domain.id,
        sync = sync,
        templateId = templateId,
        startTime = domain.startTime,
        endTime = domain.endTime,
        note = domain.note,
        date = domain.date
    )
}
