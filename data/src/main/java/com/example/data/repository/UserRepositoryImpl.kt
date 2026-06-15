package com.example.data.repository

import com.example.data.local.dao.UserDao
import com.example.data.mapper.SyncMetadataMapper
import com.example.data.mapper.UserMapper
import com.example.domain.model.user.User
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getCurrentUser(): User? {
        // Cannot use Flow synchronously, return null or implement differently
        // For now, this is a limitation - use getCurrentUserFlow() instead
        return null
    }

    override fun getCurrentUserFlow(): Flow<User?> {
        return userDao.observeCurrentUser().map { entity ->
            entity?.let { UserMapper.toDomain(it) }
        }
    }

    override fun getUserByID(userId: String): Flow<User?> {
        return userDao.observeById(userId).map { entity ->
            entity?.let { UserMapper.toDomain(it) }
        }
    }

    override suspend fun updateProfile(userId: String, name: String, photoUrl: String): User {
        val existing = userDao.getById(userId) ?: throw IllegalStateException("User not found")
        val sync = SyncMetadataMapper.updatePending(existing.sync)
        val updated = existing.copy(
            sync = sync,
            name = name,
            photoUrl = photoUrl
        )
        userDao.update(updated)
        return UserMapper.toDomain(updated)
    }

    override suspend fun followUser(userId: String) {
        // TODO: implement follow logic (needs separate follow table)
    }

    override suspend fun unfollowUser(userId: String) {
        // TODO: implement unfollow logic
    }

    override fun isFollowing(userId: String): Flow<Boolean> {
        // TODO: implement
        return kotlinx.coroutines.flow.flowOf(false)
    }

    override fun getFollowers(userId: String): Flow<List<User>> {
        // TODO: implement
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }

    override fun getFollowing(userId: String): Flow<List<User>> {
        // TODO: implement
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }
}
