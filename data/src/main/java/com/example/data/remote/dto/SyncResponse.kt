package com.example.data.remote.dto

data class SyncResponse(
    val syncedIds: Map<String, String>,
    val deletedIds: List<String> = emptyList(),
    val conflicts: List<ConflictDto> = emptyList()
)

data class ConflictDto(
    val localId: String,
    val serverVersion: Long,
    val serverData: String
)
