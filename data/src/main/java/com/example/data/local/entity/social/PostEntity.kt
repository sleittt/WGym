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
    tableName = "posts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["author_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["server_id"], unique = true),
        Index(value = ["author_id"]),
        Index(value = ["created_at"])
    ]
)
data class PostEntity(
    @PrimaryKey val id: String,
    @Embedded val sync: SyncMetadata,
    @ColumnInfo(name = "author_id") val authorId: String,
    val text: String?,
    @ColumnInfo(name = "content_type") val contentType: String, // TEXT, WORKOUT, MEAL, GALLERY, MIXED
    @ColumnInfo(name = "content_json") val contentJson: String, // serialized PostContent
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLikedByCurrentUser: Boolean = false,
    val isBookmarkedByCurrentUser: Boolean = false
)
