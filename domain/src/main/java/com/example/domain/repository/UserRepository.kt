package com.example.domain.repository

import com.example.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser() : Flow<User?>
    fun getUserByID(userId: String): Flow<User?>
    suspend fun updateProfile (update: User) : User
    suspend fun followUser (userId: String)
    suspend fun unfollowUser (userId: String)
    fun isFollowing (userId: String): Flow<Boolean>
    fun getFollowers (userId: String): Flow<List<User>>
    fun getFollowing (userId: String): Flow<List<User>>
}