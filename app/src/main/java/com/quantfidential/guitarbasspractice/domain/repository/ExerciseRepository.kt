package com.quantfidential.guitarbasspractice.domain.repository

import com.quantfidential.guitarbasspractice.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getAllExercises(): Flow<List<Exercise>>
    suspend fun getExerciseById(id: String): Exercise?
    fun getExercisesByInstrument(instrument: InstrumentType): Flow<List<Exercise>>
    fun getExercisesByDifficulty(difficulty: DifficultyLevel): Flow<List<Exercise>>
    fun getExercisesByTag(tag: String): Flow<List<Exercise>>
    fun getUserExercises(userId: String): Flow<List<Exercise>>
    fun getAiGeneratedExercises(): Flow<List<Exercise>>
    suspend fun insertExercise(exercise: Exercise)
    suspend fun insertExercises(exercises: List<Exercise>)
    suspend fun updateExercise(exercise: Exercise)
    suspend fun deleteExercise(exercise: Exercise)
    suspend fun deleteExerciseById(id: String)
    suspend fun getExerciseCount(): Int
    fun searchExercises(query: String): Flow<List<Exercise>>
}

interface UserProfileRepository {
    fun getAllActiveProfiles(): Flow<List<UserProfile>>
    suspend fun getProfileById(id: String): UserProfile?
    suspend fun getActiveProfile(): UserProfile?
    suspend fun insertProfile(profile: UserProfile)
    suspend fun updateProfile(profile: UserProfile)
    suspend fun deleteProfile(profile: UserProfile)
    suspend fun setActiveProfile(id: String)
}

interface ExerciseProgressRepository {
    fun getUserProgress(userId: String): Flow<List<ExerciseProgress>>
    suspend fun getProgressForExercise(exerciseId: String, userId: String): ExerciseProgress?
    suspend fun insertProgress(progress: ExerciseProgress)
    suspend fun updateProgress(progress: ExerciseProgress)
    fun getRecentlyPracticed(userId: String): Flow<List<ExerciseProgress>>
    suspend fun getAverageCompletion(userId: String): Float
    suspend fun getTotalPracticeTime(userId: String): Int
}