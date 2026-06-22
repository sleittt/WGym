package com.example.data.repository

import com.example.data.local.dao.ExerciseDao
import com.example.data.local.dao.ExerciseTemplateDao
import com.example.data.local.dao.SetDao
import com.example.data.local.dao.WorkoutDao
import com.example.data.local.dao.WorkoutTemplateDao
import com.example.data.local.entity.workout.WorkoutTemplateWithExercises
import com.example.data.local.entity.workout.WorkoutWithExercises
import com.example.data.mapper.ExerciseMapper
import com.example.data.mapper.ExerciseTemplateMapper
import com.example.data.mapper.SetMapper
import com.example.data.mapper.SyncMetadataMapper
import com.example.data.mapper.WorkoutMapper
import com.example.data.mapper.WorkoutTemplateMapper
import com.example.domain.model.workout.ExerciseTemplate
import com.example.domain.model.workout.Workout
import com.example.domain.model.workout.WorkoutTemplate
import com.example.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val setDao: SetDao,
    private val workoutTemplateDao: WorkoutTemplateDao,
    private val exerciseTemplateDao: ExerciseTemplateDao
) : WorkoutRepository {

    // --- Exercise Templates ---
    override fun observeExerciseTemplates(): Flow<List<ExerciseTemplate>> {
        return exerciseTemplateDao.observeAll().map { list ->
            list.map { ExerciseTemplateMapper.toDomain(it) }
        }
    }

    override suspend fun getExerciseTemplateById(id: String): ExerciseTemplate? {
        return exerciseTemplateDao.getById(id.toInt())?.let { ExerciseTemplateMapper.toDomain(it) }
    }

    override suspend fun createExerciseTemplate(template: ExerciseTemplate): ExerciseTemplate {
        val entity = ExerciseTemplateMapper.toEntity(template, SyncMetadataMapper.createPending())
        val id = exerciseTemplateDao.insert(entity).toInt()
        return template.copy(id = id)
    }

    override suspend fun updateExerciseTemplate(id: String, template: ExerciseTemplate): ExerciseTemplate {
        val existing = exerciseTemplateDao.getById(id.toInt()) ?: throw IllegalStateException("Template not found")
        val sync = SyncMetadataMapper.updatePending(existing.sync)
        val entity = ExerciseTemplateMapper.toEntity(template.copy(id = id.toInt()), sync)
        exerciseTemplateDao.update(entity)
        return template.copy(id = id.toInt())
    }

    override suspend fun deleteExerciseTemplate(id: String) {
        // Каскадный soft delete: сначала soft delete связанных exercises
        exerciseDao.softDeleteByExerciseTemplateId(id.toInt())
        // Потом soft delete самого exercise template
        exerciseTemplateDao.softDelete(id.toInt())
    }

    // --- Workouts ---
    override fun observeWorkoutHistory(): Flow<List<Workout>> {
        return workoutDao.observeWithExercises().map { list ->
            list.mapNotNull { mapWorkoutWithExercises(it) }
        }
    }

    override fun getWorkoutById(workoutId: String): Flow<Workout?> {
        return workoutDao.observeById(workoutId).map { entity ->
            entity?.let {
                val withExercises = workoutDao.getWithExercises(workoutId) ?: return@map null
                mapWorkoutWithExercises(withExercises)
            }
        }
    }

    override suspend fun createWorkout(workout: Workout): Workout {
        val sync = SyncMetadataMapper.createPending()
        val templateId = workout.template.takeIf { it.id != 0 }?.id

        val workoutEntity = WorkoutMapper.toEntity(workout, templateId, sync)
        workoutDao.insert(workoutEntity)

        workout.exercises.forEach { exercise ->
            val (exEntity, setEntities) = ExerciseMapper.toEntity(
                domain = exercise,
                workoutId = workout.id,
                templateId = null,
                sync = sync
            )
            val exId = exerciseDao.insert(exEntity).toInt()
            setEntities.forEach { setEntity ->
                setDao.insert(setEntity.copy(exerciseId = exId, id = 0))
            }
        }
        return workout
    }

    override suspend fun deleteWorkout(workoutId: String) {
        workoutDao.deleteById(workoutId)
    }

    override suspend fun getWorkoutCountInPeriod(startDate: LocalDate, endDate: LocalDate): Int {
        return workoutDao.countInPeriod(startDate, endDate)
    }

    // --- Workout Templates ---
    override fun observeWorkoutTemplates(): Flow<List<WorkoutTemplate>> {
        return workoutTemplateDao.observeAllWithExercises().map { list ->
            list.mapNotNull { templateWithExercises ->
                mapTemplateWithExercises(templateWithExercises)
            }
        }
    }

    override fun getWorkoutTemplateById(workoutTemplateId: String): Flow<WorkoutTemplate?> {
        return workoutTemplateDao.observeAll().map { list ->
            list.find { it.id == workoutTemplateId.toInt() }?.let { templateEntity ->
                val withExercises = workoutTemplateDao.getWithExercises(templateEntity.id) ?: return@let null
                mapTemplateWithExercises(withExercises)
            }
        }
    }

    override suspend fun createTemplate(draft: WorkoutTemplate): WorkoutTemplate {
        val sync = SyncMetadataMapper.createPending()
        val templateEntity = WorkoutTemplateMapper.toEntity(draft, sync)
        val templateId = workoutTemplateDao.insert(templateEntity).toInt()

        draft.exercise.forEach { exercise ->
            val (exEntity, setEntities) = ExerciseMapper.toEntity(
                domain = exercise,
                workoutId = null,
                templateId = templateId,
                sync = sync
            )
            val exId = exerciseDao.insert(exEntity).toInt()
            setEntities.forEach { setEntity ->
                setDao.insert(setEntity.copy(exerciseId = exId, id = 0))
            }
        }
        return draft.copy(id = templateId)
    }

    override suspend fun updateTemplate(id: String, updated: WorkoutTemplate): WorkoutTemplate {
        val existing = workoutTemplateDao.getById(id.toInt()) ?: throw IllegalStateException("Template not found")
        val sync = SyncMetadataMapper.updatePending(existing.sync)
        val entity = WorkoutTemplateMapper.toEntity(updated.copy(id = id.toInt()), sync)
        workoutTemplateDao.update(entity)

        exerciseDao.deleteByTemplateId(id.toInt())

        updated.exercise.forEach { exercise ->
            val (exEntity, setEntities) = ExerciseMapper.toEntity(
                domain = exercise,
                workoutId = null,
                templateId = id.toInt(),
                sync = sync
            )
            val exId = exerciseDao.insert(exEntity).toInt()
            setEntities.forEach { setEntity ->
                setDao.insert(setEntity.copy(exerciseId = exId, id = 0))
            }
        }
        return updated.copy(id = id.toInt())
    }

    override suspend fun deleteTemplate(id: String) {
        workoutTemplateDao.softDelete(id.toInt())
    }

    override suspend fun incrementTemplateUseCount(template: WorkoutTemplate) {
        workoutTemplateDao.incrementUseCount(template.id)
    }

    override suspend fun setTemplatePinned(id: String, isPinned: Boolean) {
        workoutTemplateDao.setPinned(id.toInt(), isPinned)
    }

    // --- Private helpers ---
    private suspend fun mapWorkoutWithExercises(workoutWithExercises: WorkoutWithExercises): Workout? {
        val templates = exerciseTemplateDao.getAll().associateBy { it.id }

        // Фильтруем exercises с удалёнными шаблонами, не крашимся
        val domainExercises = workoutWithExercises.exercises.mapNotNull { exWithSets ->
            val template = templates[exWithSets.exercise.exerciseTemplateId]
            if (template == null) {
                // Шаблон упражнения удалён - пропускаем это упражнение
                return@mapNotNull null
            }
            ExerciseMapper.toDomain(exWithSets, template)
        }

        val templateEntity = workoutWithExercises.workout.templateId?.let {
            workoutTemplateDao.getById(it)
        }
        val domainTemplate = templateEntity?.let { entity ->
            WorkoutTemplateMapper.toDomain(entity, emptyList())
        } ?: WorkoutTemplate(0, "", 0, false, emptyList(), false)

        return WorkoutMapper.toDomain(workoutWithExercises.workout, domainTemplate, domainExercises)
    }

    private suspend fun mapTemplateWithExercises(templateWithExercises: WorkoutTemplateWithExercises): WorkoutTemplate? {
        val templates = exerciseTemplateDao.getAll().associateBy { it.id }

        // Фильтруем exercises с удалёнными шаблонами, не крашимся
        val domainExercises = templateWithExercises.exercises.mapNotNull { exWithSets ->
            val template = templates[exWithSets.exercise.exerciseTemplateId]
            if (template == null) {
                // Шаблон упражнения удалён - пропускаем это упражнение
                return@mapNotNull null
            }
            ExerciseMapper.toDomain(exWithSets, template)
        }

        return WorkoutTemplateMapper.toDomain(templateWithExercises.template, domainExercises)
    }
}