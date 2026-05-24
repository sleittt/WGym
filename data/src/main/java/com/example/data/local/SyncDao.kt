package com.example.data.local

import androidx.room.Dao
import androidx.room.Query
import com.example.data.local.entity.common.SyncStatus
import com.example.data.local.entity.meal.FoodItemEntity
import com.example.data.local.entity.meal.MealEntity
import com.example.data.local.entity.social.CommentEntity
import com.example.data.local.entity.social.PostEntity
import com.example.data.local.entity.user.UserEntity
import com.example.data.local.entity.workout.ExerciseEntity
import com.example.data.local.entity.workout.ExerciseTemplateEntity
import com.example.data.local.entity.workout.SetEntity
import com.example.data.local.entity.workout.WorkoutEntity
import com.example.data.local.entity.workout.WorkoutTemplateEntity

@Dao
interface SyncDao {

    // --- Pending CREATE ---
    @Query("SELECT * FROM food_items WHERE sync_status = 'PENDING_CREATE'")
    suspend fun getPendingCreateFoodItems(): List<FoodItemEntity>

    @Query("SELECT * FROM meals WHERE sync_status = 'PENDING_CREATE'")
    suspend fun getPendingCreateMeals(): List<MealEntity>

    @Query("SELECT * FROM exercises WHERE sync_status = 'PENDING_CREATE'")
    suspend fun getPendingCreateExercises(): List<ExerciseEntity>

    @Query("SELECT * FROM sets WHERE sync_status = 'PENDING_CREATE'")
    suspend fun getPendingCreateSets(): List<SetEntity>

    @Query("SELECT * FROM workouts WHERE sync_status = 'PENDING_CREATE'")
    suspend fun getPendingCreateWorkouts(): List<WorkoutEntity>

    @Query("SELECT * FROM workout_templates WHERE sync_status = 'PENDING_CREATE'")
    suspend fun getPendingCreateWorkoutTemplates(): List<WorkoutTemplateEntity>

    @Query("SELECT * FROM exercise_templates WHERE sync_status = 'PENDING_CREATE'")
    suspend fun getPendingCreateExerciseTemplates(): List<ExerciseTemplateEntity>

    @Query("SELECT * FROM posts WHERE sync_status = 'PENDING_CREATE'")
    suspend fun getPendingCreatePosts(): List<PostEntity>

    @Query("SELECT * FROM comments WHERE sync_status = 'PENDING_CREATE'")
    suspend fun getPendingCreateComments(): List<CommentEntity>

    // --- Pending UPDATE ---
    @Query("SELECT * FROM food_items WHERE sync_status = 'PENDING_UPDATE'")
    suspend fun getPendingUpdateFoodItems(): List<FoodItemEntity>

    @Query("SELECT * FROM meals WHERE sync_status = 'PENDING_UPDATE'")
    suspend fun getPendingUpdateMeals(): List<MealEntity>

    @Query("SELECT * FROM workouts WHERE sync_status = 'PENDING_UPDATE'")
    suspend fun getPendingUpdateWorkouts(): List<WorkoutEntity>

    @Query("SELECT * FROM posts WHERE sync_status = 'PENDING_UPDATE'")
    suspend fun getPendingUpdatePosts(): List<PostEntity>

    @Query("SELECT * FROM comments WHERE sync_status = 'PENDING_UPDATE'")
    suspend fun getPendingUpdateComments(): List<CommentEntity>

    // --- Pending DELETE ---
    @Query("SELECT * FROM food_items WHERE sync_status = 'PENDING_DELETE'")
    suspend fun getPendingDeleteFoodItems(): List<FoodItemEntity>

    @Query("SELECT * FROM meals WHERE sync_status = 'PENDING_DELETE'")
    suspend fun getPendingDeleteMeals(): List<MealEntity>

    @Query("SELECT * FROM workouts WHERE sync_status = 'PENDING_DELETE'")
    suspend fun getPendingDeleteWorkouts(): List<WorkoutEntity>

    @Query("SELECT * FROM posts WHERE sync_status = 'PENDING_DELETE'")
    suspend fun getPendingDeletePosts(): List<PostEntity>

    @Query("SELECT * FROM comments WHERE sync_status = 'PENDING_DELETE'")
    suspend fun getPendingDeleteComments(): List<CommentEntity>

    // --- Mark synced by server id ---
    @Query("UPDATE food_items SET sync_status = 'SYNCED', server_id = :serverId WHERE id = :localId")
    suspend fun markFoodItemSynced(localId: String, serverId: String)

    @Query("UPDATE meals SET sync_status = 'SYNCED', server_id = :serverId WHERE id = :localId")
    suspend fun markMealSynced(localId: String, serverId: String)

    @Query("UPDATE workouts SET sync_status = 'SYNCED', server_id = :serverId WHERE id = :localId")
    suspend fun markWorkoutSynced(localId: String, serverId: String)

    @Query("UPDATE posts SET sync_status = 'SYNCED', server_id = :serverId WHERE id = :localId")
    suspend fun markPostSynced(localId: String, serverId: String)

    // --- Conflicts ---
    @Query("SELECT * FROM food_items WHERE sync_status = 'CONFLICT'")
    suspend fun getConflictFoodItems(): List<FoodItemEntity>

    @Query("SELECT * FROM workouts WHERE sync_status = 'CONFLICT'")
    suspend fun getConflictWorkouts(): List<WorkoutEntity>

    @Query("UPDATE food_items SET sync_status = :status WHERE id = :id")
    suspend fun updateFoodItemSyncStatus(id: String, status: SyncStatus)

    @Query("UPDATE meals SET sync_status = :status WHERE id = :id")
    suspend fun updateMealSyncStatus(id: String, status: SyncStatus)

    @Query("UPDATE workouts SET sync_status = :status WHERE id = :id")
    suspend fun updateWorkoutSyncStatus(id: String, status: SyncStatus)

    @Query("UPDATE posts SET sync_status = :status WHERE id = :id")
    suspend fun updatePostSyncStatus(id: String, status: SyncStatus)

    @Query("UPDATE comments SET sync_status = :status WHERE id = :id")
    suspend fun updateCommentSyncStatus(id: String, status: SyncStatus)
}
