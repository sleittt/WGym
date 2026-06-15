package com.example.data.mapper

import com.example.data.local.entity.common.SyncMetadata
import com.example.data.local.entity.workout.ExerciseTemplateEntity
import com.example.domain.model.workout.ExerciseTemplate

object ExerciseTemplateMapper {

    fun toDomain(entity: ExerciseTemplateEntity): ExerciseTemplate = ExerciseTemplate(
        id = entity.id,
        name = entity.name,
        description = entity.description,
        muscleGroups = entity.muscleGroups,
        isDeleted = entity.isDeleted
    )

    fun toEntity(domain: ExerciseTemplate, sync: SyncMetadata): ExerciseTemplateEntity = ExerciseTemplateEntity(
        id = if (domain.id == 0) 0 else domain.id,
        sync = sync,
        name = domain.name,
        description = domain.description,
        muscleGroups = domain.muscleGroups,
        isDeleted = domain.isDeleted
    )
}
