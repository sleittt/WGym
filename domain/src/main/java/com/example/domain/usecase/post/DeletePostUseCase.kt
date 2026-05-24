package com.example.domain.usecase.post

import com.example.domain.exception.AuthRequiredException
import com.example.domain.exception.NotAuthorException
import com.example.domain.exception.PostNotFoundException
import com.example.domain.repository.PostRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
)     {
    suspend operator fun invoke(postId : String): Result<Unit>{
        val currentUser = userRepository.getCurrentUser() ?: return Result.failure(
            AuthRequiredException()
        )
        val post = postRepository.getPostById(postId).first() ?: return Result.failure(
            PostNotFoundException()
        )
        if (post.authorId != currentUser.id){
            return Result.failure(NotAuthorException())
        }
        return try {
            postRepository.deletePost(postId)
            Result.success(Unit)
        } catch (e : Exception){
            Result.failure(e)
        }
    }
}