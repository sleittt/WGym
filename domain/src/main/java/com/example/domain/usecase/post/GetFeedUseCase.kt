package com.example.domain.usecase.post

import com.example.domain.model.social.Post
import com.example.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFeedUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(): Flow<List<Post>> = postRepository.getFeedPosts()
}