package com.example.domain.usecase.post

import com.example.domain.exception.AuthRequiredException
import com.example.domain.exception.CommentTooLongException
import com.example.domain.exception.EmptyCommentException
import com.example.domain.exception.PostNotFoundException
import com.example.domain.model.social.Comment
import com.example.domain.repository.PostRepository
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(text: String, postId: String) : Result<Comment>{
        if (text.isBlank()){
            return Result.failure(EmptyCommentException())
        }
        if (text.length > 300){
            return Result.failure(CommentTooLongException())
        }
        val post = postRepository.getPostById(postId).first() ?: return Result.failure(
            PostNotFoundException()
        )
        val currentUser = userRepository.getCurrentUser() ?: return Result.failure(
            AuthRequiredException())
        return try {
            val comment = postRepository.addComment(postId,text)
            Result.success(comment)
        }
        catch (e: Exception){
            Result.failure(e)
        }
    }
}