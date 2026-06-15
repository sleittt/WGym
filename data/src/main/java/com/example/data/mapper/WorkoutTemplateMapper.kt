package com.example.data.mapper

import com.example.data.local.entity.common.SyncMetadata
import com.example.data.local.entity.workout.WorkoutTemplateEntity
import com.example.domain.model.workout.Exercise
import com.example.domain.model.workout.WorkoutTemplate

object WorkoutTemplateMapper {

    fun toDomain(entity: WorkoutTemplateEntity, exercises: List<Exercise>): WorkoutTemplate = WorkoutTemplate(
        id = entity.id,
        name = entity.name,
        useCount = entity.useCount,
        exercise = exercises,
        isDeleted = entity.isDeleted
    )

    fun toEntity(domain: WorkoutTemplate, sync: SyncMetadata): WorkoutTemplateEntity = WorkoutTemplateEntity(
        id = if (domain.id == 0) 0 else domain.id,
        sync = sync,
        name = domain.name,
        useCount = domain.useCount,
        isDeleted = domain.isDeleted
    )
}
