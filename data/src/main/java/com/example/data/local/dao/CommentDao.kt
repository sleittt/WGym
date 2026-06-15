package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.social.CommentEntity
import com.example.data.local.entity.social.CommentWithAuthor
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE post_id = :postId ORDER BY created_at DESC")
    fun observeByPostId(postId: String): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE parent_comment_id = :parentId ORDER BY created_at DESC")
    fun observeReplies(parentId: String): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE id = :id")
    fun observeById(id: String): Flow<CommentEntity?>

    @Query("SELECT * FROM comments WHERE id = :id")
    suspend fun getById(id: String): CommentEntity?

    @Transaction
    @Query("SELECT * FROM comments WHERE post_id = :postId ORDER BY created_at DESC")
    fun observeByPostIdWithAuthors(postId: String): Flow<List<CommentWithAuthor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<CommentEntity>)

    @Update
    suspend fun update(comment: CommentEntity)

    @Delete
    suspend fun delete(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE comments SET replyCount = replyCount + 1 WHERE id = :id")
    suspend fun incrementReplyCount(id: String)

    @Query("SELECT * FROM comments WHERE sync_status != 'SYNCED'")
    suspend fun getUnsynced(): List<CommentEntity>
}
