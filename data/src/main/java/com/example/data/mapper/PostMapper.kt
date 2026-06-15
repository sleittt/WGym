package com.example.data.mapper

import com.example.data.local.entity.common.SyncMetadata
import com.example.data.local.entity.social.PostEntity
import com.example.domain.model.social.Post
import com.example.domain.model.social.PostContent

object PostMapper {

    fun toDomain(entity: PostEntity, content: PostContent?): Post = Post(
        id = entity.id,
        authorId = entity.authorId,
        text = entity.text,
        contentType = content ?: PostContent.TextPost(""),
        createdAt = entity.sync.createdAt,
        likesCount = entity.likesCount,
        commentsCount = entity.commentsCount,
        isLikedByCurrentUser = entity.isLikedByCurrentUser,
        isBookmarkedByCurrentUser = entity.isBookmarkedByCurrentUser
    )

    fun toEntity(domain: Post, sync: SyncMetadata): PostEntity = PostEntity(
        id = domain.id,
        sync = sync,
        authorId = domain.authorId,
        text = domain.text,
        contentType = PostContentMapper.toContentType(domain.contentType),
        contentJson = PostContentMapper.toJson(domain.contentType),
        likesCount = domain.likesCount,
        commentsCount = domain.commentsCount,
        isLikedByCurrentUser = domain.isLikedByCurrentUser,
        isBookmarkedByCurrentUser = domain.isBookmarkedByCurrentUser
    )
}
