package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.converter.Converters
import com.example.data.local.entity.meal.FoodItemEntity
import com.example.data.local.entity.meal.MealEntity
import com.example.data.local.entity.meal.MealItemEntity
import com.example.data.local.entity.social.CommentEntity
import com.example.data.local.entity.social.PostDraftEntity
import com.example.data.local.entity.social.PostEntity
import com.example.data.local.entity.user.UserEntity
import com.example.data.local.entity.workout.ExerciseEntity
import com.example.data.local.entity.workout.ExerciseTemplateEntity
import com.example.data.local.entity.workout.SetEntity
import com.example.data.local.entity.workout.WorkoutEntity
import com.example.data.local.entity.workout.WorkoutTemplateEntity

@Database(
    entities = [
        FoodItemEntity::class,
        MealEntity::class,
        MealItemEntity::class,
        ExerciseTemplateEntity::class,
        ExerciseEntity::class,
        SetEntity::class,
        WorkoutTemplateEntity::class,
        WorkoutEntity::class,
        UserEntity::class,
        PostEntity::class,
        PostDraftEntity::class,
        CommentEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun syncDao(): SyncDao
    // TODO: add specific DAOs
}
