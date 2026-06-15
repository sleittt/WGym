package com.example.data.remote.dto

import com.example.data.local.entity.common.SyncMetadata

data class SyncRequest<T>(
    val items: List<T>,
    val deviceId: String? = null,
    val lastSyncTimestamp: Long? = null
)
