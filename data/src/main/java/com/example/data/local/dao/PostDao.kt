package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.social.PostEntity
import com.example.data.local.entity.social.PostWithAuthor

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    suspend fun getFeed(limit: Int, offset: Int): List<PostEntity>

    @Query("SELECT * FROM posts WHERE author_id = :authorId ORDER BY created_at DESC")
    suspend fun getByAuthor(authorId: String): List<PostEntity>

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getById(id: String): PostEntity?

    @Transaction
    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getWithAuthor(id: String): PostWithAuthor?

    @Transaction
    @Query("SELECT * FROM posts ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    suspend fun getFeedWithAuthors(limit: Int, offset: Int): List<PostWithAuthor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Update
    suspend fun update(post: PostEntity)

    @Delete
    suspend fun delete(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE posts SET likesCount = likesCount + 1, isLikedByCurrentUser = 1 WHERE id = :id")
    suspend fun incrementLikes(id: String)

    @Query("UPDATE posts SET likesCount = MAX(0, likesCount - 1), isLikedByCurrentUser = 0 WHERE id = :id")
    suspend fun decrementLikes(id: String)

    @Query("UPDATE posts SET isBookmarkedByCurrentUser = :bookmarked WHERE id = :id")
    suspend fun setBookmarked(id: String, bookmarked: Boolean)

    @Query("SELECT * FROM posts WHERE sync_status != 'SYNCED'")
    suspend fun getUnsynced(): List<PostEntity>
}
