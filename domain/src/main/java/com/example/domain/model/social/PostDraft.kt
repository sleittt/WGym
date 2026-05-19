package com.example.domain.model.social

data class PostDraft(
    val type: PostType,
    val text: String?,
    val imageUrl: String?,
    val contentSnapshot: PostContent?
)
