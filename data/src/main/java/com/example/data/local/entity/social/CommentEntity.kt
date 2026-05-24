package com.example.data.local.entity.social

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.local.entity.common.SyncMetadata
import com.example.data.local.entity.user.UserEntity

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["post_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["author_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CommentEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_comment_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["server_id"], unique = true),
        Index(value = ["post_id"]),
        Index(value = ["author_id"]),
        Index(value = ["parent_comment_id"])
    ]
)
data class CommentEntity(
    @PrimaryKey val id: String,
    @Embedded val sync: SyncMetadata,
    @ColumnInfo(name = "post_id") val postId: String,
    @ColumnInfo(name = "author_id") val authorId: String,
    val text: String,
    @ColumnInfo(name = "parent_comment_id") val parentCommentId: String? = null,
    val replyCount: Int = 0
)
