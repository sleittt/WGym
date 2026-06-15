package com.example.data.mapper

import com.example.domain.model.social.PostContent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object PostContentMapper {

    private val json = Json { ignoreUnknownKeys = true }

    fun toJson(content: PostContent): String = json.encodeToString(content)

    fun fromJson(jsonString: String): PostContent? =
        runCatching { json.decodeFromString<PostContent>(jsonString) }.getOrNull()

    fun toContentType(content: PostContent): String = when (content) {
        is PostContent.TextPost -> "TEXT"
        is PostContent.WorkoutPost -> "WORKOUT"
        is PostContent.MealPost -> "MEAL"
        is PostContent.GalleryPost -> "GALLERY"
        is PostContent.MixedPost -> "MIXED"
    }
}
