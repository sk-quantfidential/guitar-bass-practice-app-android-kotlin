package com.quantfidential.guitarbasspractice.data.database

import androidx.room.*
import com.quantfidential.guitarbasspractice.data.model.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: String): ExerciseEntity?

    @Query("SELECT * FROM exercises WHERE instrument = :instrument")
    fun getExercisesByInstrument(instrument: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE difficulty = :difficulty")
    fun getExercisesByDifficulty(difficulty: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE tags LIKE :tag")
    fun getExercisesByTag(tag: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE creatorId = :userId")
    fun getUserExercises(userId: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE isAiGenerated = 1")
    fun getAiGeneratedExercises(): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun deleteExerciseById(id: String)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getExerciseCount(): Int

    @Query("SELECT * FROM exercises WHERE title LIKE :searchQuery OR description LIKE :searchQuery OR tags LIKE :searchQuery")
    fun searchExercises(searchQuery: String): Flow<List<ExerciseEntity>>
}