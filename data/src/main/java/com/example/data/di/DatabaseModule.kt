package com.example.data.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.local.SyncDao
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "wgym_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMealDao(db: AppDatabase): MealDao = db.mealDao()

    @Provides
    fun provideMealItemDao(db: AppDatabase): MealItemDao = db.mealItemDao()

    @Provides
    fun provideFoodItemDao(db: AppDatabase): FoodItemDao = db.foodItemDao()

    @Provides
    fun provideWorkoutDao(db: AppDatabase): WorkoutDao = db.workoutDao()

    @Provides
    fun provideExerciseDao(db: AppDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun provideSetDao(db: AppDatabase): SetDao = db.setDao()

    @Provides
    fun provideWorkoutTemplateDao(db: AppDatabase): WorkoutTemplateDao = db.workoutTemplateDao()

    @Provides
    fun provideExerciseTemplateDao(db: AppDatabase): ExerciseTemplateDao = db.exerciseTemplateDao()

    @Provides
    fun providePostDao(db: AppDatabase): PostDao = db.postDao()

    @Provides
    fun providePostDraftDao(db: AppDatabase): PostDraftDao = db.postDraftDao()

    @Provides
    fun provideCommentDao(db: AppDatabase): CommentDao = db.commentDao()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideSyncDao(db: AppDatabase): SyncDao = db.syncDao()
}
