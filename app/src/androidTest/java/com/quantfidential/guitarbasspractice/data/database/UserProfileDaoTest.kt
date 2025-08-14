package com.quantfidential.guitarbasspractice.data.database

// import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.quantfidential.guitarbasspractice.data.model.UserProfileEntity
import com.quantfidential.guitarbasspractice.data.model.ExerciseProgressEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class UserProfileDaoTest {

    // @get:Rule
    // val instantTaskExecutorRule = InstantTaskExecutorRule() // Not needed for these tests

    private lateinit var database: GuitarBassPracticeDatabase
    private lateinit var userProfileDao: UserProfileDao
    private lateinit var exerciseProgressDao: ExerciseProgressDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GuitarBassPracticeDatabase::class.java
        ).allowMainThreadQueries().build()
        
        userProfileDao = database.userProfileDao()
        exerciseProgressDao = database.exerciseProgressDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetProfile() = runBlocking {
        val profile = createTestProfile("test-profile-1", "John Doe", true)
        
        userProfileDao.insertProfile(profile)
        
        val retrievedProfile = userProfileDao.getProfileById("test-profile-1")
        assertNotNull(retrievedProfile)
        assertEquals("John Doe", retrievedProfile?.name)
        assertEquals("GUITAR", retrievedProfile?.primaryInstrument)
        assertTrue(retrievedProfile?.isActive ?: false)
    }

    @Test
    fun getAllActiveProfiles() = runBlocking {
        val activeProfile1 = createTestProfile("active-1", "Active User 1", true)
        val activeProfile2 = createTestProfile("active-2", "Active User 2", true)
        val inactiveProfile = createTestProfile("inactive-1", "Inactive User", false)
        
        userProfileDao.insertProfile(activeProfile1)
        userProfileDao.insertProfile(activeProfile2)
        userProfileDao.insertProfile(inactiveProfile)
        
        val activeProfiles = userProfileDao.getAllActiveProfiles().first()
        assertEquals(2, activeProfiles.size)
        assertTrue(activeProfiles.all { it.isActive })
        assertTrue(activeProfiles.any { it.name == "Active User 1" })
        assertTrue(activeProfiles.any { it.name == "Active User 2" })
    }

    @Test
    fun getActiveProfile() = runBlocking {
        val timestamp1 = System.currentTimeMillis() - 1000
        val timestamp2 = System.currentTimeMillis()
        
        val olderProfile = createTestProfile("older", "Older Profile", true).copy(createdTimestamp = timestamp1)
        val newerProfile = createTestProfile("newer", "Newer Profile", true).copy(createdTimestamp = timestamp2)
        val inactiveProfile = createTestProfile("inactive", "Inactive Profile", false)
        
        userProfileDao.insertProfile(olderProfile)
        userProfileDao.insertProfile(newerProfile)
        userProfileDao.insertProfile(inactiveProfile)
        
        val activeProfile = userProfileDao.getActiveProfile()
        assertNotNull(activeProfile)
        assertEquals("Newer Profile", activeProfile?.name) // Should return the most recent active profile
    }

    @Test
    fun updateProfile() = runBlocking {
        val profile = createTestProfile("update-test", "Original Name", true)
        userProfileDao.insertProfile(profile)
        
        val updatedProfile = profile.copy(
            name = "Updated Name",
            primaryInstrument = "BASS",
            skillLevel = "ADVANCED"
        )
        
        userProfileDao.updateProfile(updatedProfile)
        
        val retrievedProfile = userProfileDao.getProfileById("update-test")
        assertEquals("Updated Name", retrievedProfile?.name)
        assertEquals("BASS", retrievedProfile?.primaryInstrument)
        assertEquals("ADVANCED", retrievedProfile?.skillLevel)
    }

    @Test
    fun deleteProfile() = runBlocking {
        val profile = createTestProfile("delete-test", "To Delete", true)
        userProfileDao.insertProfile(profile)
        
        val beforeDelete = userProfileDao.getProfileById("delete-test")
        assertNotNull(beforeDelete)
        
        userProfileDao.deleteProfile(profile)
        
        val afterDelete = userProfileDao.getProfileById("delete-test")
        assertNull(afterDelete)
    }

    @Test
    fun deactivateOtherProfiles() = runBlocking {
        val profile1 = createTestProfile("profile-1", "User 1", true)
        val profile2 = createTestProfile("profile-2", "User 2", true)
        val profile3 = createTestProfile("profile-3", "User 3", true)
        
        userProfileDao.insertProfile(profile1)
        userProfileDao.insertProfile(profile2)
        userProfileDao.insertProfile(profile3)
        
        // All should be active initially
        val allActive = userProfileDao.getAllActiveProfiles().first()
        assertEquals(3, allActive.size)
        
        // Deactivate all except profile-2
        userProfileDao.deactivateOtherProfiles("profile-2")
        
        val activeAfterDeactivation = userProfileDao.getAllActiveProfiles().first()
        assertEquals(1, activeAfterDeactivation.size)
        assertEquals("User 2", activeAfterDeactivation[0].name)
        
        // Verify the other profiles are indeed deactivated
        val profile1After = userProfileDao.getProfileById("profile-1")
        val profile3After = userProfileDao.getProfileById("profile-3")
        assertFalse(profile1After?.isActive ?: true)
        assertFalse(profile3After?.isActive ?: true)
    }

    @Test
    fun activateProfile() = runBlocking {
        val profile = createTestProfile("activate-test", "To Activate", false)
        userProfileDao.insertProfile(profile)
        
        val beforeActivation = userProfileDao.getProfileById("activate-test")
        assertFalse(beforeActivation?.isActive ?: true)
        
        userProfileDao.activateProfile("activate-test")
        
        val afterActivation = userProfileDao.getProfileById("activate-test")
        assertTrue(afterActivation?.isActive ?: false)
    }

    @Test
    fun replaceOnConflict() = runBlocking {
        val originalProfile = createTestProfile("conflict-test", "Original", true)
        userProfileDao.insertProfile(originalProfile)
        
        val conflictProfile = createTestProfile("conflict-test", "Replaced", false).copy(
            primaryInstrument = "BASS"
        )
        userProfileDao.insertProfile(conflictProfile) // Should replace due to OnConflictStrategy.REPLACE
        
        val retrievedProfile = userProfileDao.getProfileById("conflict-test")
        assertEquals("Replaced", retrievedProfile?.name)
        assertEquals("BASS", retrievedProfile?.primaryInstrument)
        assertFalse(retrievedProfile?.isActive ?: true)
    }

    @Test
    fun nonExistentProfile() = runBlocking {
        val retrievedProfile = userProfileDao.getProfileById("non-existent-id")
        assertNull(retrievedProfile)
    }

    @Test
    fun noActiveProfiles() = runBlocking {
        val inactiveProfile1 = createTestProfile("inactive-1", "Inactive 1", false)
        val inactiveProfile2 = createTestProfile("inactive-2", "Inactive 2", false)
        
        userProfileDao.insertProfile(inactiveProfile1)
        userProfileDao.insertProfile(inactiveProfile2)
        
        val activeProfiles = userProfileDao.getAllActiveProfiles().first()
        assertTrue(activeProfiles.isEmpty())
        
        val activeProfile = userProfileDao.getActiveProfile()
        assertNull(activeProfile)
    }

    private fun createTestProfile(id: String, name: String, isActive: Boolean): UserProfileEntity {
        return UserProfileEntity(
            id = id,
            name = name,
            primaryInstrument = "GUITAR",
            skillLevel = "INTERMEDIATE",
            preferredKeys = """["C", "G", "D"]""",
            favoriteGenres = """["Rock", "Blues"]""",
            createdTimestamp = System.currentTimeMillis(),
            isActive = isActive
        )
    }
}

@RunWith(AndroidJUnit4::class)
class ExerciseProgressDaoTest {

    // @get:Rule
    // val instantTaskExecutorRule = InstantTaskExecutorRule() // Not needed for these tests

    private lateinit var database: GuitarBassPracticeDatabase
    private lateinit var exerciseProgressDao: ExerciseProgressDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GuitarBassPracticeDatabase::class.java
        ).allowMainThreadQueries().build()
        
        exerciseProgressDao = database.exerciseProgressDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetProgress() = runBlocking {
        val progress = createTestProgress("progress-1", "exercise-1", "user-1", 75)
        
        exerciseProgressDao.insertProgress(progress)
        
        val retrievedProgress = exerciseProgressDao.getProgressForExercise("exercise-1", "user-1")
        assertNotNull(retrievedProgress)
        assertEquals(75, retrievedProgress?.completionPercentage ?: 0)
        assertEquals("exercise-1", retrievedProgress?.exerciseId)
        assertEquals("user-1", retrievedProgress?.userId)
    }

    @Test
    fun getUserProgress() = runBlocking {
        val progress1 = createTestProgress("progress-1", "exercise-1", "user-1", 80)
        val progress2 = createTestProgress("progress-2", "exercise-2", "user-1", 65)
        val progress3 = createTestProgress("progress-3", "exercise-1", "user-2", 90)
        
        exerciseProgressDao.insertProgress(progress1)
        exerciseProgressDao.insertProgress(progress2)
        exerciseProgressDao.insertProgress(progress3)
        
        val user1Progress = exerciseProgressDao.getUserProgress("user-1").first()
        assertEquals(2, user1Progress.size)
        assertTrue(user1Progress.all { it.userId == "user-1" })
        
        val user2Progress = exerciseProgressDao.getUserProgress("user-2").first()
        assertEquals(1, user2Progress.size)
        assertEquals("user-2", user2Progress[0].userId)
    }

    @Test
    fun updateProgress() = runBlocking {
        val progress = createTestProgress("update-test", "exercise-1", "user-1", 50)
        exerciseProgressDao.insertProgress(progress)
        
        val updatedProgress = progress.copy(
            completionPercentage = 85,
            bestAccuracy = 92,
            practiceCount = 10
        )
        
        exerciseProgressDao.updateProgress(updatedProgress)
        
        val retrievedProgress = exerciseProgressDao.getProgressForExercise("exercise-1", "user-1")
        assertEquals(85, retrievedProgress?.completionPercentage ?: 0)
        assertEquals(92, retrievedProgress?.bestAccuracy ?: 0)
        assertEquals(10, retrievedProgress?.practiceCount ?: 0)
    }

    @Test
    fun getRecentlyPracticed() = runBlocking {
        val currentTime = System.currentTimeMillis()
        
        val progress1 = createTestProgress("recent-1", "exercise-1", "user-1", 80)
            .copy(lastPracticed = currentTime - 1000) // 1 second ago
        val progress2 = createTestProgress("recent-2", "exercise-2", "user-1", 65)
            .copy(lastPracticed = currentTime - 2000) // 2 seconds ago
        val progress3 = createTestProgress("recent-3", "exercise-3", "user-1", 90)
            .copy(lastPracticed = currentTime - 500)  // 0.5 seconds ago (most recent)
        
        exerciseProgressDao.insertProgress(progress1)
        exerciseProgressDao.insertProgress(progress2)
        exerciseProgressDao.insertProgress(progress3)
        
        val recentlyPracticed = exerciseProgressDao.getRecentlyPracticed("user-1").first()
        
        assertEquals(3, recentlyPracticed.size)
        
        // Should be ordered by lastPracticed DESC (most recent first)
        assertEquals("exercise-3", recentlyPracticed[0].exerciseId)
        assertEquals("exercise-1", recentlyPracticed[1].exerciseId)
        assertEquals("exercise-2", recentlyPracticed[2].exerciseId)
    }

    @Test
    fun getRecentlyPracticedLimit() = runBlocking {
        val currentTime = System.currentTimeMillis()
        
        // Insert 15 progress records
        repeat(15) { index ->
            val progress = createTestProgress("recent-$index", "exercise-$index", "user-1", 50)
                .copy(lastPracticed = currentTime - (index * 1000L))
            exerciseProgressDao.insertProgress(progress)
        }
        
        val recentlyPracticed = exerciseProgressDao.getRecentlyPracticed("user-1").first()
        
        // Should limit to 10 results
        assertEquals(10, recentlyPracticed.size)
    }

    @Test
    fun getAverageCompletion() = runBlocking {
        val progress1 = createTestProgress("avg-1", "exercise-1", "user-1", 80)
        val progress2 = createTestProgress("avg-2", "exercise-2", "user-1", 60)
        val progress3 = createTestProgress("avg-3", "exercise-3", "user-1", 100)
        
        exerciseProgressDao.insertProgress(progress1)
        exerciseProgressDao.insertProgress(progress2)
        exerciseProgressDao.insertProgress(progress3)
        
        val averageCompletion = exerciseProgressDao.getAverageCompletion("user-1")
        
        assertEquals(80f, averageCompletion) // (80 + 60 + 100) / 3 = 80
    }

    @Test
    fun getAverageCompletionNoProgress() = runBlocking {
        val averageCompletion = exerciseProgressDao.getAverageCompletion("user-with-no-progress")
        
        assertEquals(0f, averageCompletion) // Should return 0 for no progress
    }

    @Test
    fun getTotalPracticeTime() = runBlocking {
        val progress1 = createTestProgress("time-1", "exercise-1", "user-1", 80)
            .copy(practiceTimeSeconds = 1800) // 30 minutes
        val progress2 = createTestProgress("time-2", "exercise-2", "user-1", 60)
            .copy(practiceTimeSeconds = 2400) // 40 minutes
        val progress3 = createTestProgress("time-3", "exercise-3", "user-1", 100)
            .copy(practiceTimeSeconds = 600)  // 10 minutes
        
        exerciseProgressDao.insertProgress(progress1)
        exerciseProgressDao.insertProgress(progress2)
        exerciseProgressDao.insertProgress(progress3)
        
        val totalPracticeTime = exerciseProgressDao.getTotalPracticeTime("user-1")
        
        assertEquals(4800, totalPracticeTime) // 30 + 40 + 10 = 80 minutes = 4800 seconds
    }

    @Test
    fun getTotalPracticeTimeNoProgress() = runBlocking {
        val totalPracticeTime = exerciseProgressDao.getTotalPracticeTime("user-with-no-progress")
        
        assertEquals(0, totalPracticeTime)
    }

    @Test
    fun replaceOnConflict() = runBlocking {
        val originalProgress = createTestProgress("conflict-test", "exercise-1", "user-1", 50)
        exerciseProgressDao.insertProgress(originalProgress)
        
        val conflictProgress = createTestProgress("conflict-test", "exercise-1", "user-1", 85)
            .copy(bestAccuracy = 95, practiceCount = 5)
        exerciseProgressDao.insertProgress(conflictProgress) // Should replace
        
        val retrievedProgress = exerciseProgressDao.getProgressForExercise("exercise-1", "user-1")
        assertEquals(85, retrievedProgress?.completionPercentage ?: 0)
        assertEquals(95, retrievedProgress?.bestAccuracy ?: 0)
        assertEquals(5, retrievedProgress?.practiceCount ?: 0)
    }

    @Test
    fun nonExistentProgress() = runBlocking {
        val retrievedProgress = exerciseProgressDao.getProgressForExercise("non-existent-exercise", "non-existent-user")
        assertNull(retrievedProgress)
    }

    private fun createTestProgress(id: String, exerciseId: String, userId: String, completionPercentage: Int): ExerciseProgressEntity {
        return ExerciseProgressEntity(
            id = id,
            exerciseId = exerciseId,
            userId = userId,
            completionPercentage = completionPercentage,
            bestAccuracy = 88,
            practiceTimeSeconds = 1200, // 20 minutes
            lastPracticed = System.currentTimeMillis(),
            practiceCount = 3,
            mistakeFrets = """[3, 7, 12]"""
        )
    }
}