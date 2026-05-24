package com.example.data.local.entity.social

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.local.entity.user.UserEntity

data class CommentWithAuthor(
    @Embedded val comment: CommentEntity,
    @Relation(
        parentColumn = "author_id",
        entityColumn = "id"
    )
    val author: UserEntity?
)
