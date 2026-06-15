package com.example.data.mapper

import com.example.data.local.entity.social.PostDraftEntity
import com.example.domain.model.social.PostContent
import com.example.domain.model.social.PostDraft
import kotlin.time.Clock
import kotlin.time.Instant

object PostDraftMapper {

    fun toDomain(entity: PostDraftEntity, content: PostContent?): PostDraft = PostDraft(
        //type = entity.type,
        text = entity.text,
        imageUrl = entity.imageUrl,
        contentSnapshot = content,
        authorId = entity.authorId
    )

    fun toEntity(domain: PostDraft, id: String, createdAt: Instant = Clock.System.now()): PostDraftEntity = PostDraftEntity(
        id = id,
        //type = domain.type,
        text = domain.text,
        imageUrl = domain.imageUrl,
        contentJson = domain.contentSnapshot?.let { PostContentMapper.toJson(it) },
        authorId = domain.authorId,
        createdAt = createdAt,
        updatedAt = Clock.System.now()
    )
}
