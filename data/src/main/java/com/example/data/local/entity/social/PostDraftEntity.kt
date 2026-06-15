package com.example.data.local.entity.social

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
//import com.example.domain.model.social.PostType
import kotlinx.datetime.Instant

@Entity(
    tableName = "post_drafts",
    indices = [Index(value = ["author_id"])]
)
data class PostDraftEntity(
    @PrimaryKey val id: String,
    //val type: PostType,
    val text: String?,
    val imageUrl: String?,
    @ColumnInfo(name = "content_json") val contentJson: String?,
    @ColumnInfo(name = "author_id") val authorId: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
