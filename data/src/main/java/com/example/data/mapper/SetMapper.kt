package com.example.data.mapper

import com.example.data.local.entity.common.SyncMetadata
import com.example.data.local.entity.workout.SetEntity
import com.example.domain.model.workout.Set

object SetMapper {

    fun toDomain(entity: SetEntity): Set = Set(
        id = entity.id,
        note = entity.note,
        rest = entity.rest,
        load = entity.load,
        reps = entity.reps,
        type = entity.type,
        isCompleted = entity.isCompleted
    )

    fun toEntity(domain: Set, exerciseId: Int, sync: SyncMetadata): SetEntity = SetEntity(
        id = if (domain.id == 0) 0 else domain.id,
        sync = sync,
        exerciseId = exerciseId, // <-- ЭТО ПОЛЕ ДОЛЖНО БЫТЬ!
        rest = domain.rest,
        load = domain.load,
        reps = domain.reps,
        type = domain.type,
        isCompleted = domain.isCompleted
    )
}
