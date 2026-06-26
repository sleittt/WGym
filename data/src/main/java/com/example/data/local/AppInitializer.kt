package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.domain.model.workout.ExerciseTemplate
import com.example.domain.model.workout.MuscleGroup
import com.example.domain.usecase.workout.CreateExerciseTemplateUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val createExerciseTemplate: CreateExerciseTemplateUseCase
) {
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("app_prefs") }
    )
    private val isSeededKey = booleanPreferencesKey("exercises_seeded_v1")

    suspend fun initialize() {
        val prefs = dataStore.data.first()
        val alreadySeeded = prefs[isSeededKey] ?: false
        if (alreadySeeded) return

        seedExercises()
        markSeeded()
    }

    private suspend fun markSeeded() {
        dataStore.edit { prefs ->
            prefs[isSeededKey] = true
        }
    }

    private suspend fun seedExercises() {
        val exercises = listOf(
            // === ГРУДЬ ===
            ExerciseTemplate(0, "Жим штанги лёжа", "Классическое базовое упражнение для грудных мышц", listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS), false),
            ExerciseTemplate(0, "Жим гантелей лёжа", "Упражнение для грудных с большей амплитудой", listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS), false),
            ExerciseTemplate(0, "Сведение рук в кроссовере", "Изолирующее упражнение для грудных мышц", listOf(MuscleGroup.CHEST), false),
            ExerciseTemplate(0, "Разведение гантелей лёжа", "Растяжение грудных мышц", listOf(MuscleGroup.CHEST), false),
            ExerciseTemplate(0, "Отжимания на брусьях", "Базовое упражнение на нижнюю часть груди и трицепс", listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS), false),
            ExerciseTemplate(0, "Жим штанги в наклоне", "Акцент на верхнюю часть груди", listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS), false),
            ExerciseTemplate(0, "Пуловер с гантелью", "Растяжение грудной клетки", listOf(MuscleGroup.CHEST, MuscleGroup.BACK), false),

            // === СПИНА ===
            ExerciseTemplate(0, "Подтягивания", "Базовое упражнение для широчайших мышц спины", listOf(MuscleGroup.BACK, MuscleGroup.BICEPS), false),
            ExerciseTemplate(0, "Тяга штанги в наклоне", "Классическая тяга для массы спины", listOf(MuscleGroup.BACK, MuscleGroup.BICEPS, MuscleGroup.FOREARMS), false),
            ExerciseTemplate(0, "Тяга верхнего блока к груди", "Упражнение для широчайших мышц", listOf(MuscleGroup.BACK, MuscleGroup.BICEPS), false),
            ExerciseTemplate(0, "Тяга нижнего блока сидя", "Акцент на нижнюю часть широчайших", listOf(MuscleGroup.BACK, MuscleGroup.BICEPS), false),
            ExerciseTemplate(0, "Тяга гантели в наклоне одной рукой", "Упражнение для толщины спины", listOf(MuscleGroup.BACK, MuscleGroup.BICEPS), false),
            ExerciseTemplate(0, "Становая тяга", "Королевское упражнение для всей задней цепи", listOf(MuscleGroup.BACK, MuscleGroup.LEGS, MuscleGroup.GLUTES, MuscleGroup.FOREARMS), false),
            ExerciseTemplate(0, "Гиперэкстензия", "Укрепление поясницы и ягодиц", listOf(MuscleGroup.BACK, MuscleGroup.GLUTES, MuscleGroup.LEGS), false),
            ExerciseTemplate(0, "Тяга Т-грифа", "Упражнение для толщины спины", listOf(MuscleGroup.BACK, MuscleGroup.BICEPS, MuscleGroup.FOREARMS), false),
            ExerciseTemplate(0, "Шраги со штангой", "Упражнение для трапеций", listOf(MuscleGroup.BACK, MuscleGroup.SHOULDERS), false),
            ExerciseTemplate(0, "Шраги с гантелями", "Упражнение для трапеций", listOf(MuscleGroup.BACK, MuscleGroup.SHOULDERS), false),

            // === НОГИ ===
            ExerciseTemplate(0, "Приседания со штангой", "Базовое упражнение для ног", listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES), false),
            ExerciseTemplate(0, "Приседания с гантелями", "Приседания с гантелями у плеч", listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES), false),
            ExerciseTemplate(0, "Жим ногами", "Упражнение в тренажёре для квадрицепсов", listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES), false),
            ExerciseTemplate(0, "Выпады с гантелями", "Упражнение для ног и ягодиц", listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES), false),
            ExerciseTemplate(0, "Выпады со штангой", "Базовые выпады со штангой на плечах", listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES), false),
            ExerciseTemplate(0, "Разгибание ног сидя", "Изоляция квадрицепсов", listOf(MuscleGroup.LEGS), false),
            ExerciseTemplate(0, "Сгибание ног лёжа", "Изоляция бицепса бедра", listOf(MuscleGroup.LEGS), false),
            ExerciseTemplate(0, "Сгибание ног сидя", "Изоляция бицепса бедра", listOf(MuscleGroup.LEGS), false),
            ExerciseTemplate(0, "Сведение ног в тренажёре", "Упражнение для приводящих мышц бедра", listOf(MuscleGroup.LEGS), false),
            ExerciseTemplate(0, "Разведение ног в тренажёре", "Упражнение для отводящих мышц бедра", listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES), false),
            ExerciseTemplate(0, "Болгарские сплит-приседания", "Одноногие приседания на скамье", listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES), false),
            ExerciseTemplate(0, "Румынская тяга", "Упражнение для задней поверхности бедра и ягодиц", listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES, MuscleGroup.BACK), false),
            ExerciseTemplate(0, "Сумо-приседания", "Широкая постановка ног, акцент на внутреннюю поверхность бедра", listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES), false),
            ExerciseTemplate(0, "Гакк-приседания", "Приседания в тренажёре Гаккеншмидта", listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES), false),

            // === ПЛЕЧИ ===
            ExerciseTemplate(0, "Жим штанги стоя", "Базовое упражнение для плеч", listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS), false),
            ExerciseTemplate(0, "Жим гантелей сидя", "Упражнение для плеч сидя на скамье", listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS), false),
            ExerciseTemplate(0, "Арнольд-жим", "Жим гантелей с вращением кистей", listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS), false),
            ExerciseTemplate(0, "Махи гантелями в стороны", "Изоляция среднего пучка дельтовидных", listOf(MuscleGroup.SHOULDERS), false),
            ExerciseTemplate(0, "Махи гантелями вперёд", "Упражнение для переднего пучка плеч", listOf(MuscleGroup.SHOULDERS), false),
            ExerciseTemplate(0, "Разведение гантелей в наклоне", "Упражнение для заднего пучка плеч", listOf(MuscleGroup.SHOULDERS, MuscleGroup.BACK), false),
            ExerciseTemplate(0, "Тяга штанги к подбородку", "Упражнение для трапеций и переднего пучка", listOf(MuscleGroup.SHOULDERS, MuscleGroup.BACK), false),
            ExerciseTemplate(0, "Обратные разведения в кроссовере", "Изоляция заднего пучка дельтовидных", listOf(MuscleGroup.SHOULDERS, MuscleGroup.BACK), false),

            // === БИЦЕПС ===
            ExerciseTemplate(0, "Подъём штанги на бицепс", "Базовое упражнение для бицепса", listOf(MuscleGroup.BICEPS, MuscleGroup.FOREARMS), false),
            ExerciseTemplate(0, "Подъём гантелей на бицепс", "Упражнение с разведением кистей", listOf(MuscleGroup.BICEPS, MuscleGroup.FOREARMS), false),
            ExerciseTemplate(0, "Молотки", "Подъём гантелей нейтральным хватом", listOf(MuscleGroup.BICEPS, MuscleGroup.FOREARMS), false),
            ExerciseTemplate(0, "Концентрированный подъём", "Подъём гантели сидя на скамье", listOf(MuscleGroup.BICEPS), false),
            ExerciseTemplate(0, "Подъём на бицепс в блоке", "Упражнение в кроссовере", listOf(MuscleGroup.BICEPS), false),
            ExerciseTemplate(0, "Скотта-подъём", "Подъём на бицепс на скамье Скотта", listOf(MuscleGroup.BICEPS), false),
            ExerciseTemplate(0, "Подъём штанги обратным хватом", "Акцент на брахиалис и предплечья", listOf(MuscleGroup.BICEPS, MuscleGroup.FOREARMS), false),

            // === ТРИЦЕПС ===
            ExerciseTemplate(0, "Французский жим лёжа", "Изоляция трицепса со штангой или EZ-грифом", listOf(MuscleGroup.TRICEPS), false),
            ExerciseTemplate(0, "Французский жим сидя", "Французский жим с гантелью за головой", listOf(MuscleGroup.TRICEPS), false),
            ExerciseTemplate(0, "Разгибание рук в блоке", "Изоляция трицепса в кроссовере", listOf(MuscleGroup.TRICEPS), false),
            ExerciseTemplate(0, "Разгибание руки в блоке одной рукой", "Одностороннее разгибание в кроссовере", listOf(MuscleGroup.TRICEPS), false),
            ExerciseTemplate(0, "Отжимания узким хватом", "Отжимания с узкой постановкой рук", listOf(MuscleGroup.TRICEPS, MuscleGroup.CHEST), false),
            ExerciseTemplate(0, "Разгибание рук с гантелью из-за головы", "Двухстороннее разгибание с гантелью", listOf(MuscleGroup.TRICEPS), false),

            // === ПРЕСС ===
            ExerciseTemplate(0, "Скручивания", "Базовое упражнение для прямой мышцы живота", listOf(MuscleGroup.ABS), false),
            ExerciseTemplate(0, "Подъёмы ног в упоре", "Упражнение для нижнего пресса", listOf(MuscleGroup.ABS), false),
            ExerciseTemplate(0, "Подъёмы корпуса на скамье", "Скручивания с отрицательным углом", listOf(MuscleGroup.ABS), false),
            ExerciseTemplate(0, "Косые скручивания", "Скручивания с поворотом корпуса", listOf(MuscleGroup.ABS), false),
            ExerciseTemplate(0, "Велосипед", "Динамическое упражнение для косых мышц", listOf(MuscleGroup.ABS), false),
            ExerciseTemplate(0, "Подъёмы ног лёжа", "Упражнение для нижнего пресса", listOf(MuscleGroup.ABS), false),

            // === ЯГОДИЦЫ ===
            ExerciseTemplate(0, "Мостик", "Подъём таза лёжа на спине", listOf(MuscleGroup.GLUTES, MuscleGroup.LEGS), false),
            ExerciseTemplate(0, "Мостик на одной ноге", "Односторонний мостик", listOf(MuscleGroup.GLUTES, MuscleGroup.LEGS), false),
            ExerciseTemplate(0, "Выпады назад", "Обратные выпады с акцентом на ягодицы", listOf(MuscleGroup.GLUTES, MuscleGroup.LEGS), false),
            ExerciseTemplate(0, "Отведение ноги назад в блоке", "Изоляция ягодиц в кроссовере", listOf(MuscleGroup.GLUTES), false),
            ExerciseTemplate(0, "Гиперэкстензия с поворотом", "Упражнение для ягодиц и поясницы", listOf(MuscleGroup.GLUTES, MuscleGroup.BACK), false),

            // === ИКРЫ ===
            ExerciseTemplate(0, "Подъёмы на носки стоя", "Упражнение для икроножных мышц", listOf(MuscleGroup.CALVES), false),
            ExerciseTemplate(0, "Подъёмы на носки сидя", "Акцент на камбаловидную мышцу", listOf(MuscleGroup.CALVES), false),
            ExerciseTemplate(0, "Подъёмы на носки в тренажёре", "Упражнение в тренажёре для икроножных", listOf(MuscleGroup.CALVES), false),
            ExerciseTemplate(0, "Подъёмы на носки в тренажёре сидя", "Изоляция икроножных сидя", listOf(MuscleGroup.CALVES), false),

            // === ПРЕДПЛЕЧЬЯ ===
            ExerciseTemplate(0, "Сгибание запястий со штангой", "Упражнение для сгибателей предплечий", listOf(MuscleGroup.FOREARMS), false),
            ExerciseTemplate(0, "Разгибание запястий со штангой", "Упражнение для разгибателей предплечий", listOf(MuscleGroup.FOREARMS), false),
            ExerciseTemplate(0, "Сгибание запястий с гантелями", "Альтернатива со штангой", listOf(MuscleGroup.FOREARMS), false),
            ExerciseTemplate(0, "Скручивание полотенца", "Упражнение для силы хвата", listOf(MuscleGroup.FOREARMS), false),
            ExerciseTemplate(0, "Вис на перекладине", "Упражнение для силы хвата", listOf(MuscleGroup.FOREARMS), false)
        )

        exercises.forEach { createExerciseTemplate(it) }
    }
}