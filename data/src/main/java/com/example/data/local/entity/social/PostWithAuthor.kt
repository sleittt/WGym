package com.example.data.local.entity.social

import androidx.room.Embedded
import androidx.room.Relation
import com.example.data.local.entity.user.UserEntity

data class PostWithAuthor(
    @Embedded val post: PostEntity,
    @Relation(
        parentColumn = "author_id",
        entityColumn = "id"
    )
    val author: UserEntity?
)
