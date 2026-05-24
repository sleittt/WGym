package com.example.domain.repository

import com.example.domain.model.workout.ExerciseTemplate
import com.example.domain.model.workout.Workout
import com.example.domain.model.workout.WorkoutTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface WorkoutRepository {
    //Шаблоны
    fun observeExerciseTemplates() : Flow<List<ExerciseTemplate>>
    fun getExerciseTemplateById(id: String): ExerciseTemplate?
    suspend fun createExerciseTemplate(template: ExerciseTemplate) : ExerciseTemplate
    suspend fun updateExerciseTemplate(id: String,template: ExerciseTemplate): ExerciseTemplate
    suspend fun deleteExerciseTemplate(id: String)
    //Тренировки
    fun observeWorkoutHistory() : Flow<List<Workout>>
    fun getWorkoutById(workoutId: String): Flow<Workout?>
    suspend fun createWorkout(workout: Workout): Workout
    suspend fun deleteWorkout(workoutId: String)
    suspend fun getWorkoutCountInPeriod(startDate: LocalDate, endDate: LocalDate): Int
    //Шаблоны тренировок
    fun observeWorkoutTemplates(): Flow<List<WorkoutTemplate>>
    fun getWorkoutTemplateById(workoutTemplateId: String) : Flow<WorkoutTemplate?>
    suspend fun createTemplate(draft: WorkoutTemplate) : WorkoutTemplate
    suspend fun updateTemplate(id: String, updated: WorkoutTemplate) : WorkoutTemplate
    suspend fun deleteTemplate(id: String)
    suspend fun incrementTemplateUseCount(template: WorkoutTemplate)
}