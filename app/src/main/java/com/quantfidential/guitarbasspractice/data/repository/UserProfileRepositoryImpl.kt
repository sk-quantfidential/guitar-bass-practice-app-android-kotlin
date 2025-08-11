package com.quantfidential.guitarbasspractice.data.repository

import com.quantfidential.guitarbasspractice.data.database.UserProfileDao
import com.quantfidential.guitarbasspractice.data.database.ExerciseProgressDao
import com.quantfidential.guitarbasspractice.domain.model.UserProfile
import com.quantfidential.guitarbasspractice.domain.model.ExerciseProgress
import com.quantfidential.guitarbasspractice.domain.repository.UserProfileRepository
import com.quantfidential.guitarbasspractice.domain.repository.ExerciseProgressRepository
import com.quantfidential.guitarbasspractice.util.toDomain
import com.quantfidential.guitarbasspractice.util.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    override fun getAllActiveProfiles(): Flow<List<UserProfile>> {
        return userProfileDao.getAllActiveProfiles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProfileById(id: String): UserProfile? {
        return userProfileDao.getProfileById(id)?.toDomain()
    }

    override suspend fun getActiveProfile(): UserProfile? {
        return userProfileDao.getActiveProfile()?.toDomain()
    }

    override suspend fun insertProfile(profile: UserProfile) {
        userProfileDao.insertProfile(profile.toEntity())
    }

    override suspend fun updateProfile(profile: UserProfile) {
        userProfileDao.updateProfile(profile.toEntity())
    }

    override suspend fun deleteProfile(profile: UserProfile) {
        userProfileDao.deleteProfile(profile.toEntity())
    }

    override suspend fun setActiveProfile(id: String) {
        userProfileDao.deactivateOtherProfiles(id)
        userProfileDao.activateProfile(id)
    }
}

@Singleton
class ExerciseProgressRepositoryImpl @Inject constructor(
    private val exerciseProgressDao: ExerciseProgressDao
) : ExerciseProgressRepository {

    override fun getUserProgress(userId: String): Flow<List<ExerciseProgress>> {
        return exerciseProgressDao.getUserProgress(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProgressForExercise(exerciseId: String, userId: String): ExerciseProgress? {
        return exerciseProgressDao.getProgressForExercise(exerciseId, userId)?.toDomain()
    }

    override suspend fun insertProgress(progress: ExerciseProgress) {
        exerciseProgressDao.insertProgress(progress.toEntity())
    }

    override suspend fun updateProgress(progress: ExerciseProgress) {
        exerciseProgressDao.updateProgress(progress.toEntity())
    }

    override fun getRecentlyPracticed(userId: String): Flow<List<ExerciseProgress>> {
        return exerciseProgressDao.getRecentlyPracticed(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAverageCompletion(userId: String): Float {
        return exerciseProgressDao.getAverageCompletion(userId)
    }

    override suspend fun getTotalPracticeTime(userId: String): Int {
        return exerciseProgressDao.getTotalPracticeTime(userId)
    }
}