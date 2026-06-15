package com.example.data.mapper

import com.example.data.local.entity.common.SyncMetadata
import com.example.data.local.entity.user.UserEntity
import com.example.domain.model.user.User

object UserMapper {

    fun toDomain(entity: UserEntity): User = User(
        id = entity.id,
        name = entity.name,
        photoUrl = entity.photoUrl,
        bio = entity.bio
    )

    fun toEntity(domain: User, sync: SyncMetadata): UserEntity = UserEntity(
        id = domain.id,
        sync = sync,
        name = domain.name,
        photoUrl = domain.photoUrl,
        bio = domain.bio
    )
}
