package com.example.domain.repository

import com.example.domain.model.social.Comment
import com.example.domain.model.social.Post
import com.example.domain.model.social.PostDraft
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun getFeedPosts(): Flow<List<Post>>
    suspend fun getPostById(postId: String) : Flow<Post?>
    suspend fun createPost(draft: PostDraft) : Post
    suspend fun deletePost(postId: String)
    suspend fun getComments(postId: String) : Flow<List<Comment>>
    suspend fun addComment(postId: String, text: String) : Comment
    suspend fun deleteComment(commentId: String)
    suspend fun setBookmarked(postId: String, isBookmarked: Boolean)
    suspend fun syncFeed()
    suspend fun setLiked(postId: String, isLiked: Boolean)
    //TODO прописать лайк/дизлайк в дата
    suspend fun isLikedByCurrentUser() : Boolean
}