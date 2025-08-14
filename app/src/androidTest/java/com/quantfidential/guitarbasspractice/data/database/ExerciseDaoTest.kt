package com.quantfidential.guitarbasspractice.data.database

// import androidx.arch.core.executor.testing.InstantTaskExecutorRule // Not needed for these tests
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.quantfidential.guitarbasspractice.data.model.ExerciseEntity
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
class ExerciseDaoTest {

    // @get:Rule
    // val instantTaskExecutorRule = InstantTaskExecutorRule() // Not needed for these tests

    private lateinit var database: GuitarBassPracticeDatabase
    private lateinit var exerciseDao: ExerciseDao

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GuitarBassPracticeDatabase::class.java
        ).allowMainThreadQueries().build()
        
        exerciseDao = database.exerciseDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetExercise() = runBlocking {
        val exercise = createTestExercise("test-id-1", "Test Exercise")
        
        exerciseDao.insertExercise(exercise)
        
        val retrievedExercise = exerciseDao.getExerciseById("test-id-1")
        assertNotNull(retrievedExercise)
        assertEquals("Test Exercise", retrievedExercise?.title)
        assertEquals("GUITAR", retrievedExercise?.instrument)
    }

    @Test
    fun getAllExercises() = runBlocking {
        val exercise1 = createTestExercise("test-id-1", "Exercise 1")
        val exercise2 = createTestExercise("test-id-2", "Exercise 2")
        
        exerciseDao.insertExercise(exercise1)
        exerciseDao.insertExercise(exercise2)
        
        val allExercises = exerciseDao.getAllExercises().first()
        assertEquals(2, allExercises.size)
        assertTrue(allExercises.any { it.title == "Exercise 1" })
        assertTrue(allExercises.any { it.title == "Exercise 2" })
    }

    @Test
    fun getExercisesByInstrument() = runBlocking {
        val guitarExercise = createTestExercise("guitar-1", "Guitar Exercise").copy(instrument = "GUITAR")
        val bassExercise = createTestExercise("bass-1", "Bass Exercise").copy(instrument = "BASS")
        
        exerciseDao.insertExercise(guitarExercise)
        exerciseDao.insertExercise(bassExercise)
        
        val guitarExercises = exerciseDao.getExercisesByInstrument("GUITAR").first()
        assertEquals(1, guitarExercises.size)
        assertEquals("Guitar Exercise", guitarExercises[0].title)
        
        val bassExercises = exerciseDao.getExercisesByInstrument("BASS").first()
        assertEquals(1, bassExercises.size)
        assertEquals("Bass Exercise", bassExercises[0].title)
    }

    @Test
    fun getExercisesByDifficulty() = runBlocking {
        val beginnerExercise = createTestExercise("beginner-1", "Beginner Exercise").copy(difficulty = "BEGINNER")
        val intermediateExercise = createTestExercise("intermediate-1", "Intermediate Exercise").copy(difficulty = "INTERMEDIATE")
        
        exerciseDao.insertExercise(beginnerExercise)
        exerciseDao.insertExercise(intermediateExercise)
        
        val beginnerExercises = exerciseDao.getExercisesByDifficulty("BEGINNER").first()
        assertEquals(1, beginnerExercises.size)
        assertEquals("Beginner Exercise", beginnerExercises[0].title)
    }

    @Test
    fun getExercisesByTag() = runBlocking {
        val scaleExercise = createTestExercise("scale-1", "Scale Exercise").copy(tags = """["scale", "practice"]""")
        val chordExercise = createTestExercise("chord-1", "Chord Exercise").copy(tags = """["chord", "harmony"]""")
        
        exerciseDao.insertExercise(scaleExercise)
        exerciseDao.insertExercise(chordExercise)
        
        val scaleExercises = exerciseDao.getExercisesByTag("%scale%").first()
        assertEquals(1, scaleExercises.size)
        assertEquals("Scale Exercise", scaleExercises[0].title)
    }

    @Test
    fun getUserExercises() = runBlocking {
        val userExercise = createTestExercise("user-1", "User Exercise").copy(creatorId = "user-123")
        val systemExercise = createTestExercise("system-1", "System Exercise").copy(creatorId = "system")
        
        exerciseDao.insertExercise(userExercise)
        exerciseDao.insertExercise(systemExercise)
        
        val userExercises = exerciseDao.getUserExercises("user-123").first()
        assertEquals(1, userExercises.size)
        assertEquals("User Exercise", userExercises[0].title)
    }

    @Test
    fun getAiGeneratedExercises() = runBlocking {
        val aiExercise = createTestExercise("ai-1", "AI Exercise").copy(isAiGenerated = true, aiPrompt = "Create a blues exercise")
        val manualExercise = createTestExercise("manual-1", "Manual Exercise").copy(isAiGenerated = false)
        
        exerciseDao.insertExercise(aiExercise)
        exerciseDao.insertExercise(manualExercise)
        
        val aiExercises = exerciseDao.getAiGeneratedExercises().first()
        assertEquals(1, aiExercises.size)
        assertEquals("AI Exercise", aiExercises[0].title)
        assertEquals("Create a blues exercise", aiExercises[0].aiPrompt)
    }

    @Test
    fun insertMultipleExercises() = runBlocking {
        val exercises = listOf(
            createTestExercise("batch-1", "Batch Exercise 1"),
            createTestExercise("batch-2", "Batch Exercise 2"),
            createTestExercise("batch-3", "Batch Exercise 3")
        )
        
        exerciseDao.insertExercises(exercises)
        
        val allExercises = exerciseDao.getAllExercises().first()
        assertEquals(3, allExercises.size)
    }

    @Test
    fun updateExercise() = runBlocking {
        val exercise = createTestExercise("update-test", "Original Title")
        exerciseDao.insertExercise(exercise)
        
        val updatedExercise = exercise.copy(
            title = "Updated Title",
            description = "Updated description",
            modifiedTimestamp = System.currentTimeMillis()
        )
        
        exerciseDao.updateExercise(updatedExercise)
        
        val retrievedExercise = exerciseDao.getExerciseById("update-test")
        assertEquals("Updated Title", retrievedExercise?.title)
        assertEquals("Updated description", retrievedExercise?.description)
    }

    @Test
    fun deleteExercise() = runBlocking {
        val exercise = createTestExercise("delete-test", "To Delete")
        exerciseDao.insertExercise(exercise)
        
        val beforeDelete = exerciseDao.getExerciseById("delete-test")
        assertNotNull(beforeDelete)
        
        exerciseDao.deleteExercise(exercise)
        
        val afterDelete = exerciseDao.getExerciseById("delete-test")
        assertNull(afterDelete)
    }

    @Test
    fun deleteExerciseById() = runBlocking {
        val exercise = createTestExercise("delete-by-id-test", "To Delete By ID")
        exerciseDao.insertExercise(exercise)
        
        exerciseDao.deleteExerciseById("delete-by-id-test")
        
        val afterDelete = exerciseDao.getExerciseById("delete-by-id-test")
        assertNull(afterDelete)
    }

    @Test
    fun getExerciseCount() = runBlocking {
        assertEquals(0, exerciseDao.getExerciseCount())
        
        exerciseDao.insertExercise(createTestExercise("count-1", "Exercise 1"))
        assertEquals(1, exerciseDao.getExerciseCount())
        
        exerciseDao.insertExercise(createTestExercise("count-2", "Exercise 2"))
        assertEquals(2, exerciseDao.getExerciseCount())
    }

    @Test
    fun searchExercises() = runBlocking {
        val exercise1 = createTestExercise("search-1", "Blues Scale Exercise").copy(
            description = "Learn the blues scale",
            tags = """["blues", "scale"]"""
        )
        val exercise2 = createTestExercise("search-2", "Rock Chord Exercise").copy(
            description = "Practice rock chords",
            tags = """["rock", "chords"]"""
        )
        val exercise3 = createTestExercise("search-3", "Jazz Theory").copy(
            description = "Advanced jazz concepts",
            tags = """["jazz", "theory"]"""
        )
        
        exerciseDao.insertExercises(listOf(exercise1, exercise2, exercise3))
        
        // Search by title
        val bluesResults = exerciseDao.searchExercises("%Blues%").first()
        assertEquals(1, bluesResults.size)
        assertEquals("Blues Scale Exercise", bluesResults[0].title)
        
        // Search by description
        val advancedResults = exerciseDao.searchExercises("%Advanced%").first()
        assertEquals(1, advancedResults.size)
        assertEquals("Jazz Theory", advancedResults[0].title)
        
        // Search by tags
        val rockResults = exerciseDao.searchExercises("%rock%").first()
        assertEquals(1, rockResults.size)
        assertEquals("Rock Chord Exercise", rockResults[0].title)
        
        // Search with no results
        val noResults = exerciseDao.searchExercises("%nonexistent%").first()
        assertTrue(noResults.isEmpty())
    }

    @Test
    fun replaceOnConflict() = runBlocking {
        val originalExercise = createTestExercise("conflict-test", "Original")
        exerciseDao.insertExercise(originalExercise)
        
        val conflictExercise = createTestExercise("conflict-test", "Replaced").copy(
            description = "This should replace the original"
        )
        exerciseDao.insertExercise(conflictExercise) // Should replace due to OnConflictStrategy.REPLACE
        
        val retrievedExercise = exerciseDao.getExerciseById("conflict-test")
        assertEquals("Replaced", retrievedExercise?.title)
        assertEquals("This should replace the original", retrievedExercise?.description)
        
        // Should still only have one exercise
        val allExercises = exerciseDao.getAllExercises().first()
        assertEquals(1, allExercises.size)
    }

    @Test
    fun nonExistentExercise() = runBlocking {
        val retrievedExercise = exerciseDao.getExerciseById("non-existent-id")
        assertNull(retrievedExercise)
    }

    @Test
    fun emptyDatabase() = runBlocking {
        val allExercises = exerciseDao.getAllExercises().first()
        assertTrue(allExercises.isEmpty())
        assertEquals(0, exerciseDao.getExerciseCount())
    }

    private fun createTestExercise(id: String, title: String): ExerciseEntity {
        return ExerciseEntity(
            id = id,
            title = title,
            description = "Test description for $title",
            instrument = "GUITAR",
            tags = """["test", "practice"]""",
            difficulty = "BEGINNER",
            fretboardConstraint = """{"minFret":0,"maxFret":12,"allowedStrings":[1,2,3,4,5,6],"numStrings":6}""",
            theoryComponent = """{"keys":["C"],"scales":["major"],"chords":[],"melodyLine":null,"intervals":[],"timeSignature":"4/4"}""",
            notation = """[{"type":"TAB","content":"","measureCount":1,"encoding":null}]""",
            playbackSettings = """{"bpm":120,"loop":false,"metronome":true,"volume":0.8,"repeatCount":1}""",
            notes = """[{"stringNumber":1,"fret":0,"beat":0,"noteName":"E","duration":1}]""",
            createdTimestamp = System.currentTimeMillis(),
            modifiedTimestamp = System.currentTimeMillis(),
            creatorId = "test-user",
            isAiGenerated = false,
            aiPrompt = null
        )
    }
}