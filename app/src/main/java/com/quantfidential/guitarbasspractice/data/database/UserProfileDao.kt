package com.quantfidential.guitarbasspractice.data.database

import androidx.room.*
import com.quantfidential.guitarbasspractice.data.model.UserProfileEntity
import com.quantfidential.guitarbasspractice.data.model.ExerciseProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE isActive = 1")
    fun getAllActiveProfiles(): Flow<List<UserProfileEntity>>

    @Query("SELECT * FROM user_profiles WHERE id = :id")
    suspend fun getProfileById(id: String): UserProfileEntity?

    @Query("SELECT * FROM user_profiles WHERE isActive = 1 ORDER BY createdTimestamp DESC LIMIT 1")
    suspend fun getActiveProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profiles SET isActive = 0 WHERE id != :activeId")
    suspend fun deactivateOtherProfiles(activeId: String)

    @Query("UPDATE user_profiles SET isActive = 1 WHERE id = :id")
    suspend fun activateProfile(id: String)
}

@Dao
interface ExerciseProgressDao {
    @Query("SELECT * FROM exercise_progress WHERE userId = :userId")
    fun getUserProgress(userId: String): Flow<List<ExerciseProgressEntity>>

    @Query("SELECT * FROM exercise_progress WHERE exerciseId = :exerciseId AND userId = :userId")
    suspend fun getProgressForExercise(exerciseId: String, userId: String): ExerciseProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ExerciseProgressEntity)

    @Update
    suspend fun updateProgress(progress: ExerciseProgressEntity)

    @Query("SELECT * FROM exercise_progress WHERE userId = :userId ORDER BY lastPracticed DESC LIMIT 10")
    fun getRecentlyPracticed(userId: String): Flow<List<ExerciseProgressEntity>>

    @Query("SELECT AVG(completionPercentage) FROM exercise_progress WHERE userId = :userId")
    suspend fun getAverageCompletion(userId: String): Float

    @Query("SELECT SUM(practiceTimeSeconds) FROM exercise_progress WHERE userId = :userId")
    suspend fun getTotalPracticeTime(userId: String): Int
}