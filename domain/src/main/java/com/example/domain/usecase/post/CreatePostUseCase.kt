package com.example.domain.usecase.post

import com.example.domain.exception.AuthRequiredException
import com.example.domain.exception.EmptyPostException
import com.example.domain.exception.PostTooLongException
import com.example.domain.model.social.Post
import com.example.domain.model.social.PostDraft
import com.example.domain.repository.PostRepository
import com.example.domain.repository.UserRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(draft: PostDraft) : Result<Post>{
        if (draft.text.isNullOrBlank() && draft.contentSnapshot==null){
            return Result.failure(EmptyPostException())
        }
        if ((draft.text?.length ?: 0) > 300){
            return Result.failure(PostTooLongException())
        }
        val currentUser = userRepository.getCurrentUser() ?: return Result.failure(
            AuthRequiredException())
        return try {
            val post = postRepository.createPost(draft.copy(authorId = currentUser.id))
            Result.success(post)
        }
        catch (e: Exception){
            Result.failure(e)
        }
    }
}