package com.example.data.mapper

import com.example.data.local.entity.common.SyncMetadata
import com.example.data.local.entity.social.CommentEntity
import com.example.domain.model.social.Comment

object CommentMapper {

    fun toDomain(entity: CommentEntity): Comment = Comment(
        id = entity.id,
        postId = entity.postId,
        authorId = entity.authorId,
        text = entity.text,
        parentCommentId = entity.parentCommentId,
        replyCount = entity.replyCount,
        createdAt = entity.sync.createdAt
    )

    fun toEntity(domain: Comment, sync: SyncMetadata): CommentEntity = CommentEntity(
        id = domain.id,
        sync = sync,
        postId = domain.postId,
        authorId = domain.authorId,
        text = domain.text,
        parentCommentId = domain.parentCommentId,
        replyCount = domain.replyCount
    )
}
