package com.example.domain.usecase.post

import com.example.domain.model.social.Post
import com.example.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPostDetailUseCase @Inject constructor (
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String) : Flow<Post?> = postRepository.getPostById(postId)
}