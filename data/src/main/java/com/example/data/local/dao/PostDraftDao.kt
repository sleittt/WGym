package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.social.PostDraftEntity

@Dao
interface PostDraftDao {

    @Query("SELECT * FROM post_drafts WHERE author_id = :authorId ORDER BY updatedAt DESC")
    suspend fun getByAuthor(authorId: String): List<PostDraftEntity>

    @Query("SELECT * FROM post_drafts WHERE id = :id")
    suspend fun getById(id: String): PostDraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(draft: PostDraftEntity)

    @Update
    suspend fun update(draft: PostDraftEntity)

    @Delete
    suspend fun delete(draft: PostDraftEntity)

    @Query("DELETE FROM post_drafts WHERE id = :id")
    suspend fun deleteById(id: String)
}
