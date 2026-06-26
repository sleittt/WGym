package com.example.domain.repository

import com.example.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getUserRole(): Flow<UserRole>
    suspend fun register(name: String, email: String, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}
