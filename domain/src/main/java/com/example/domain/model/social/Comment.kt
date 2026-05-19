package com.example.domain.model.social

import kotlin.time.Instant

data class Comment (
    val id: String,
    val postId: String,
    val authorId: String,
    val text: String,
    val parentCommentId: String? = null,
    val replyCount: Int = 0,
    val createdAt: Instant
)