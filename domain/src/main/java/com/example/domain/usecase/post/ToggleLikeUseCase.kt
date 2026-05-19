package com.example.domain.usecase.post

import com.example.domain.repository.PostRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleLikeUseCase @Inject constructor (
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String) {
        val post = postRepository.getPostById(postId).first() ?: return
        postRepository.setLiked(postId, !post.isLikedByCurrentUser)
    }
}