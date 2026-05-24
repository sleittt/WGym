package com.example.data.local.entity.user

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.local.entity.common.SyncMetadata

@Entity(
    tableName = "users",
    indices = [Index(value = ["server_id"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String,
    @Embedded val sync: SyncMetadata,
    val name: String,
    val photoUrl: String?,
    val bio: String = ""
)
