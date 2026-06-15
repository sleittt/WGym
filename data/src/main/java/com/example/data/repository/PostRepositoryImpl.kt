package com.example.data.repository

import com.example.data.local.dao.CommentDao
import com.example.data.local.dao.PostDao
import com.example.data.local.dao.PostDraftDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.user.UserEntity
import com.example.data.mapper.CommentMapper
import com.example.data.mapper.PostContentMapper
import com.example.data.mapper.PostDraftMapper
import com.example.data.mapper.PostMapper
import com.example.data.mapper.SyncMetadataMapper
import com.example.domain.model.social.Comment
import com.example.domain.model.social.Post
import com.example.domain.model.social.PostContent
import com.example.domain.model.social.PostDraft
import com.example.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Clock

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val commentDao: CommentDao,
    private val postDraftDao: PostDraftDao,
    private val userDao: UserDao
) : PostRepository {

    override suspend fun getFeedPosts(): Flow<List<Post>> {
        return postDao.observeFeedWithAuthors(100, 0).map { list ->
            list.map { postWithAuthor ->
                val content = PostContentMapper.fromJson(postWithAuthor.post.contentJson)
                PostMapper.toDomain(postWithAuthor.post, content)
            }
        }
    }

    override suspend fun getPostById(postId: String): Flow<Post?> {
        return postDao.observeById(postId).map { entity ->
            entity?.let {
                val content = PostContentMapper.fromJson(it.contentJson)
                PostMapper.toDomain(it, content)
            }
        }
    }

    override suspend fun createPost(draft: PostDraft): Post {
        val draftId = UUID.randomUUID().toString()
        postDraftDao.insert(PostDraftMapper.toEntity(draft, draftId))

        val postId = UUID.randomUUID().toString()
        val post = Post(
            id = postId,
            authorId = draft.authorId,
            text = draft.text,
            contentType = draft.contentSnapshot ?: PostContent.TextPost(draft.text ?: ""),
            createdAt = Clock.System.now(),
            likesCount = 0,
            commentsCount = 0,
            isLikedByCurrentUser = false,
            isBookmarkedByCurrentUser = false
        )
        val sync = SyncMetadataMapper.createPending()
        postDao.insert(PostMapper.toEntity(post, sync))
        return post
    }

    override suspend fun deletePost(postId: String) {
        postDao.deleteById(postId)
    }

    override suspend fun getComments(postId: String): Flow<List<Comment>> {
        return commentDao.observeByPostIdWithAuthors(postId).map { list ->
            list.map { CommentMapper.toDomain(it.comment) }
        }
    }

    override suspend fun getCommentById(postId: String, commentId: String): Flow<Comment?> {
        return commentDao.observeById(commentId).map { entity ->
            entity?.let { CommentMapper.toDomain(it) }
        }
    }

    override suspend fun addComment(postId: String, text: String): Comment {
        val commentId = UUID.randomUUID().toString()
        val currentUser = userDao.observeCurrentUser().let { flow ->
            var result: UserEntity? = null
            flow.collect { result = it }
            result
        } ?: throw IllegalStateException("No current user")

        val comment = Comment(
            id = commentId,
            postId = postId,
            authorId = currentUser.id,
            text = text,
            createdAt = Clock.System.now()
        )
        val sync = SyncMetadataMapper.createPending()
        commentDao.insert(CommentMapper.toEntity(comment, sync))
        return comment
    }

    override suspend fun deleteComment(postId: String, commentId: String) {
        commentDao.deleteById(commentId)
    }

    override suspend fun setBookmarked(postId: String, isBookmarked: Boolean) {
        postDao.setBookmarked(postId, isBookmarked)
    }

    override suspend fun syncFeed() {
        // TODO: fetch from remote API and merge
    }

    override suspend fun setLiked(postId: String, isLiked: Boolean) {
        if (isLiked) {
            postDao.incrementLikes(postId)
        } else {
            postDao.decrementLikes(postId)
        }
    }
}
