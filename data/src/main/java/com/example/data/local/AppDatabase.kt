package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.converter.Converters
import com.example.data.local.dao.CommentDao
import com.example.data.local.dao.ExerciseDao
import com.example.data.local.dao.ExerciseTemplateDao
import com.example.data.local.dao.FoodItemDao
import com.example.data.local.dao.MealDao
import com.example.data.local.dao.MealItemDao
import com.example.data.local.dao.PostDao
import com.example.data.local.dao.PostDraftDao
import com.example.data.local.dao.SetDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.WorkoutDao
import com.example.data.local.dao.WorkoutTemplateDao
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
    abstract fun mealDao(): MealDao
    abstract fun mealItemDao(): MealItemDao
    abstract fun foodItemDao(): FoodItemDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun setDao(): SetDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun exerciseTemplateDao(): ExerciseTemplateDao
    abstract fun postDao(): PostDao
    abstract fun postDraftDao(): PostDraftDao
    abstract fun commentDao(): CommentDao
    abstract fun userDao(): UserDao
    abstract fun syncDao(): SyncDao
}
