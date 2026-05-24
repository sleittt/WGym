package com.example.domain.usecase.user

import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleFollowUseCase @Inject constructor (
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String) {
        val isFollowing = userRepository.isFollowing(userId).first()
        if (isFollowing){
            userRepository.unfollowUser(userId)
        } else {
            userRepository.followUser(userId)
        }
    }
}