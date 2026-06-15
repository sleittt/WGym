package com.example.domain.model.social

import com.example.domain.model.meal.Meal
import com.example.domain.model.workout.WorkoutTemplate
import java.awt.Image
import kotlin.time.Instant

data class Post (
    val id: String,
    val authorId: String,
    val text: String?,
    val contentType: PostContent,
    val createdAt: Instant,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLikedByCurrentUser: Boolean = false,
    val isBookmarkedByCurrentUser: Boolean = false
)

sealed class PostContent {
    data class TextPost(val value: String) : PostContent()
    data class WorkoutPost(val template: WorkoutTemplate) : PostContent()
    data class MealPost(val meal: Meal) : PostContent()
    data class GalleryPost(val images: List<String>) : PostContent()    data class MixedPost(val items: List<PostContent>) : PostContent()
}

//enum class PostType {
//    WORKOUT, MEAL, ALL, NONE
//}