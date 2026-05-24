package com.example.domain.usecase.post

import com.example.domain.repository.PostRepository
import javax.inject.Inject

class SyncFeedUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(){
        postRepository.syncFeed()
    }
}