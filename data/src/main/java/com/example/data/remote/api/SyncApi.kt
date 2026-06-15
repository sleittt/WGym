package com.example.data.remote.api

import com.example.data.local.entity.meal.MealEntity
import com.example.data.local.entity.social.PostEntity
import com.example.data.local.entity.workout.WorkoutEntity
import com.example.data.remote.dto.SyncRequest
import com.example.data.remote.dto.SyncResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface SyncApi {

    @POST("api/sync/meals")
    suspend fun syncMeals(@Body request: SyncRequest<MealEntity>): SyncResponse

    @POST("api/sync/workouts")
    suspend fun syncWorkouts(@Body request: SyncRequest<WorkoutEntity>): SyncResponse

    @POST("api/sync/posts")
    suspend fun syncPosts(@Body request: SyncRequest<PostEntity>): SyncResponse

    @DELETE("api/meals/{serverId}")
    suspend fun deleteMeal(@Path("serverId") serverId: String)

    @DELETE("api/workouts/{serverId}")
    suspend fun deleteWorkout(@Path("serverId") serverId: String)

    @DELETE("api/posts/{serverId}")
    suspend fun deletePost(@Path("serverId") serverId: String)
}
