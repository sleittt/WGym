package com.example.domain.repository

import com.example.domain.model.workout.ExerciseTemplate
import com.example.domain.model.workout.MuscleGroup
import com.example.domain.model.workout.Workout
import com.example.domain.model.workout.WorkoutTemplate
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface WorkoutRepository {
    //Шаблоны
    fun observeExerciseTemplates() : Flow<List<ExerciseTemplate>>
    suspend fun getExerciseTemplateById(id: String): ExerciseTemplate?
    suspend fun createExerciseTemplate(name: String, muscleGroup: MuscleGroup)
    suspend fun updateExerciseTemplate(template: ExerciseTemplate)
    suspend fun deleteExerciseTemplate(id: String)
    //Тренировки
    fun observeWorkoutHistory() : Flow<List<Workout>>
    fun observeWorkoutById(workoutId: String): Flow<Workout?>
    suspend fun createWorkoutTemplate(): WorkoutTemplate
    suspend fun updateWorkout(workoutId: String, draft: WorkoutTemplate) : Workout
    suspend fun deleteWorkout(workoutId: String)
    suspend fun getWorkoutCountInPeriod(startDate: LocalDate, endDate: LocalDate): Int
    //Шаблоны тренировок
    fun observeWorkoutTemplates(): Flow<List<WorkoutTemplate>>
    fun observeWorkoutTemplateById(workoutTemplateId: String) : Flow<WorkoutTemplate>
    suspend fun createTemplate(draft: WorkoutTemplate) : WorkoutTemplate
    suspend fun updateTemplate(id: String) : WorkoutTemplate
    suspend fun deleteTemplate(id: String)
    suspend fun incrementUseCount(id: String)
}