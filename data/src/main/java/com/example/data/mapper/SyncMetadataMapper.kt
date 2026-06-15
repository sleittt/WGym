package com.example.data.mapper

import com.example.data.local.entity.common.SyncMetadata
import com.example.data.local.entity.common.SyncStatus
import kotlin.time.Clock
import kotlin.time.Instant

object SyncMetadataMapper {

    fun createPending(): SyncMetadata = SyncMetadata(
        serverId = null,
        syncStatus = SyncStatus.PENDING_CREATE,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now(),
        version = 0
    )

    fun updatePending(existing: SyncMetadata): SyncMetadata = existing.copy(
        syncStatus = if (existing.serverId == null) SyncStatus.PENDING_CREATE else SyncStatus.PENDING_UPDATE,
        updatedAt = Clock.System.now()
    )

    fun deletePending(existing: SyncMetadata): SyncMetadata = existing.copy(
        syncStatus = SyncStatus.PENDING_DELETE,
        updatedAt = Clock.System.now()
    )
}
