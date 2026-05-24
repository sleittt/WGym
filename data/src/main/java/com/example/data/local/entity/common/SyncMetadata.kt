package com.example.data.local.entity.common

import androidx.room.ColumnInfo
import kotlinx.datetime.Instant

data class SyncMetadata(
    @ColumnInfo(name = "server_id") val serverId: String?,
    @ColumnInfo(name = "sync_status") val syncStatus: SyncStatus,
    @ColumnInfo(name = "created_at") val createdAt: Instant,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant,
    @ColumnInfo(name = "version", defaultValue = "0") val version: Int = 0
)
