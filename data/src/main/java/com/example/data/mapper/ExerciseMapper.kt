package com.example.data.mapper

import com.example.data.local.entity.common.SyncMetadata
import com.example.data.local.entity.workout.ExerciseEntity
import com.example.data.local.entity.workout.ExerciseTemplateEntity
import com.example.data.local.entity.workout.ExerciseWithSets
import com.example.data.local.entity.workout.SetEntity
import com.example.domain.model.workout.Exercise

object ExerciseMapper {

    fun toDomain(entity: ExerciseEntity, sets: List<SetEntity>, template: ExerciseTemplateEntity): Exercise = Exercise(
        id = entity.id,
        template = ExerciseTemplateMapper.toDomain(template),
        note = entity.note,
        sets = sets.map { SetMapper.toDomain(it) },
        order = entity.order
    )

    fun toDomain(withSets: ExerciseWithSets, template: ExerciseTemplateEntity): Exercise =
        toDomain(withSets.exercise, withSets.sets, template)

    fun toEntity(domain: Exercise, workoutId: String?, templateId: Int?, sync: SyncMetadata): Pair<ExerciseEntity, List<SetEntity>> {
        val exerciseEntity = ExerciseEntity(
            id = 0, // Room генерирует
            sync = sync,
            workoutId = workoutId,
            templateId = templateId,
            exerciseTemplateId = domain.template.id,
            note = domain.note,
            order = domain.order
        )

        val setEntities = domain.sets.map {
            SetMapper.toEntity(it, exerciseId = 0, sync = sync) // exerciseId = 0, назначим после insert
        }
        return exerciseEntity to setEntities
    }
}