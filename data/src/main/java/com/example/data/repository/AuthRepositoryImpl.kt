package com.example.data.repository

import com.example.data.local.DataStoreManager
import com.example.domain.model.UserRole
import com.example.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : AuthRepository {

    override fun getUserRole(): Flow<UserRole> {
        return dataStoreManager.isRegistered.map { isRegistered ->
            if (isRegistered) UserRole.USER else UserRole.GUEST
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<Unit> {
        if (name.isBlank() || email.isBlank() || password.length < 4) {
            return Result.failure(IllegalArgumentException("Заполните все поля корректно"))
        }
        dataStoreManager.setRegistered(true)
        return Result.success(Unit)
    }

    override suspend fun logout(): Result<Unit> {
        dataStoreManager.setRegistered(false)
        return Result.success(Unit)
    }
}
