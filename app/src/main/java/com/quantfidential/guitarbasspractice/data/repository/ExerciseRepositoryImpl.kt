package com.quantfidential.guitarbasspractice.data.repository

import com.quantfidential.guitarbasspractice.data.database.ExerciseDao
import com.quantfidential.guitarbasspractice.domain.model.*
import com.quantfidential.guitarbasspractice.domain.repository.ExerciseRepository
import com.quantfidential.guitarbasspractice.data.model.ExerciseEntity
import com.quantfidential.guitarbasspractice.util.toEntity
import com.quantfidential.guitarbasspractice.util.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao
) : ExerciseRepository {

    override fun getAllExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAllExercises().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getExerciseById(id: String): Exercise? {
        return exerciseDao.getExerciseById(id)?.toDomain()
    }

    override fun getExercisesByInstrument(instrument: InstrumentType): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByInstrument(instrument.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getExercisesByDifficulty(difficulty: DifficultyLevel): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByDifficulty(difficulty.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getExercisesByTag(tag: String): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByTag("%$tag%").map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUserExercises(userId: String): Flow<List<Exercise>> {
        return exerciseDao.getUserExercises(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAiGeneratedExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAiGeneratedExercises().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertExercise(exercise: Exercise) {
        exerciseDao.insertExercise(exercise.toEntity())
    }

    override suspend fun insertExercises(exercises: List<Exercise>) {
        exerciseDao.insertExercises(exercises.map { it.toEntity() })
    }

    override suspend fun updateExercise(exercise: Exercise) {
        exerciseDao.updateExercise(exercise.toEntity())
    }

    override suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.deleteExercise(exercise.toEntity())
    }

    override suspend fun deleteExerciseById(id: String) {
        exerciseDao.deleteExerciseById(id)
    }

    override suspend fun getExerciseCount(): Int {
        return exerciseDao.getExerciseCount()
    }

    override fun searchExercises(query: String): Flow<List<Exercise>> {
        return exerciseDao.searchExercises("%$query%").map { entities ->
            entities.map { it.toDomain() }
        }
    }
}