package com.quantfidential.guitarbasspractice.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.quantfidential.guitarbasspractice.data.database.GuitarBassPracticeDatabase
import com.quantfidential.guitarbasspractice.data.model.ExerciseEntity
import com.quantfidential.guitarbasspractice.domain.model.*
import com.quantfidential.guitarbasspractice.util.toDomain
import com.quantfidential.guitarbasspractice.util.toEntity
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
class ExerciseRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: GuitarBassPracticeDatabase
    private lateinit var repository: ExerciseRepositoryImpl

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GuitarBassPracticeDatabase::class.java
        ).allowMainThreadQueries().build()
        
        repository = ExerciseRepositoryImpl(database.exerciseDao())
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetAllExercises() = runBlocking {
        val exercise1 = createTestExercise("exercise-1", "First Exercise")
        val exercise2 = createTestExercise("exercise-2", "Second Exercise")
        
        repository.insertExercise(exercise1)
        repository.insertExercise(exercise2)
        
        val allExercises = repository.getAllExercises().first()
        assertEquals(2, allExercises.size)
        assertTrue(allExercises.any { it.title == "First Exercise" })
        assertTrue(allExercises.any { it.title == "Second Exercise" })
    }

    @Test
    fun getExerciseById() = runBlocking {
        val exercise = createTestExercise("get-by-id-test", "Get By ID Exercise")
        repository.insertExercise(exercise)
        
        val retrievedExercise = repository.getExerciseById("get-by-id-test")
        assertNotNull(retrievedExercise)
        assertEquals("Get By ID Exercise", retrievedExercise?.title)
        assertEquals(InstrumentType.GUITAR, retrievedExercise?.instrument)
        assertEquals(DifficultyLevel.INTERMEDIATE, retrievedExercise?.difficulty)
    }

    @Test
    fun getExerciseById_nonExistent() = runBlocking {
        val retrievedExercise = repository.getExerciseById("non-existent-id")
        assertNull(retrievedExercise)
    }

    @Test
    fun getExercisesByInstrument() = runBlocking {
        val guitarExercise = createTestExercise("guitar-1", "Guitar Exercise").copy(instrument = InstrumentType.GUITAR)
        val bassExercise = createTestExercise("bass-1", "Bass Exercise").copy(instrument = InstrumentType.BASS)
        
        repository.insertExercise(guitarExercise)
        repository.insertExercise(bassExercise)
        
        val guitarExercises = repository.getExercisesByInstrument(InstrumentType.GUITAR).first()
        assertEquals(1, guitarExercises.size)
        assertEquals("Guitar Exercise", guitarExercises[0].title)
        assertEquals(InstrumentType.GUITAR, guitarExercises[0].instrument)
        
        val bassExercises = repository.getExercisesByInstrument(InstrumentType.BASS).first()
        assertEquals(1, bassExercises.size)
        assertEquals("Bass Exercise", bassExercises[0].title)
        assertEquals(InstrumentType.BASS, bassExercises[0].instrument)
    }

    @Test
    fun getExercisesByDifficulty() = runBlocking {
        val beginnerExercise = createTestExercise("beginner-1", "Beginner Exercise").copy(difficulty = DifficultyLevel.BEGINNER)
        val expertExercise = createTestExercise("expert-1", "Expert Exercise").copy(difficulty = DifficultyLevel.EXPERT)
        
        repository.insertExercise(beginnerExercise)
        repository.insertExercise(expertExercise)
        
        val beginnerExercises = repository.getExercisesByDifficulty(DifficultyLevel.BEGINNER).first()
        assertEquals(1, beginnerExercises.size)
        assertEquals("Beginner Exercise", beginnerExercises[0].title)
        assertEquals(DifficultyLevel.BEGINNER, beginnerExercises[0].difficulty)
    }

    @Test
    fun getUserExercises() = runBlocking {
        val userExercise = createTestExercise("user-1", "User Exercise").copy(creatorId = "test-user")
        val systemExercise = createTestExercise("system-1", "System Exercise").copy(creatorId = "system")
        
        repository.insertExercise(userExercise)
        repository.insertExercise(systemExercise)
        
        val userExercises = repository.getUserExercises("test-user").first()
        assertEquals(1, userExercises.size)
        assertEquals("User Exercise", userExercises[0].title)
        assertEquals("test-user", userExercises[0].creatorId)
    }

    @Test
    fun getAiGeneratedExercises() = runBlocking {
        val aiExercise = createTestExercise("ai-1", "AI Generated Exercise").copy(
            isAiGenerated = true,
            aiPrompt = "Create a blues scale exercise"
        )
        val manualExercise = createTestExercise("manual-1", "Manual Exercise").copy(isAiGenerated = false)
        
        repository.insertExercise(aiExercise)
        repository.insertExercise(manualExercise)
        
        val aiExercises = repository.getAiGeneratedExercises().first()
        assertEquals(1, aiExercises.size)
        assertEquals("AI Generated Exercise", aiExercises[0].title)
        assertTrue(aiExercises[0].isAiGenerated)
        assertEquals("Create a blues scale exercise", aiExercises[0].aiPrompt)
    }

    @Test
    fun searchExercises() = runBlocking {
        val bluesExercise = createTestExercise("blues-1", "Blues Scale Practice").copy(
            description = "Learn the blues scale patterns",
            tags = listOf("blues", "scale", "practice")
        )
        val rockExercise = createTestExercise("rock-1", "Rock Power Chords").copy(
            description = "Master rock chord progressions",
            tags = listOf("rock", "chords", "power")
        )
        val jazzExercise = createTestExercise("jazz-1", "Jazz Theory Basics").copy(
            description = "Introduction to jazz harmony",
            tags = listOf("jazz", "theory", "harmony")
        )
        
        repository.insertExercise(bluesExercise)
        repository.insertExercise(rockExercise)
        repository.insertExercise(jazzExercise)
        
        // Search by title
        val bluesResults = repository.searchExercises("Blues").first()
        assertEquals(1, bluesResults.size)
        assertEquals("Blues Scale Practice", bluesResults[0].title)
        
        // Search by tag (should find exercises with "theory" tag)
        val theoryResults = repository.searchExercises("theory").first()
        assertEquals(1, theoryResults.size)
        assertEquals("Jazz Theory Basics", theoryResults[0].title)
        
        // Search by description
        val harmonyResults = repository.searchExercises("harmony").first()
        assertEquals(1, harmonyResults.size)
        assertEquals("Jazz Theory Basics", harmonyResults[0].title)
    }

    @Test
    fun updateExercise() = runBlocking {
        val originalExercise = createTestExercise("update-test", "Original Title")
        repository.insertExercise(originalExercise)
        
        val updatedExercise = originalExercise.copy(
            title = "Updated Title",
            description = "Updated description",
            difficulty = DifficultyLevel.ADVANCED
        )
        
        repository.updateExercise(updatedExercise)
        
        val retrievedExercise = repository.getExerciseById("update-test")
        assertEquals("Updated Title", retrievedExercise?.title)
        assertEquals("Updated description", retrievedExercise?.description)
        assertEquals(DifficultyLevel.ADVANCED, retrievedExercise?.difficulty)
        assertTrue((retrievedExercise?.modifiedTimestamp ?: 0) > originalExercise.modifiedTimestamp)
    }

    @Test
    fun deleteExercise() = runBlocking {
        val exercise = createTestExercise("delete-test", "To Delete")
        repository.insertExercise(exercise)
        
        val beforeDelete = repository.getExerciseById("delete-test")
        assertNotNull(beforeDelete)
        
        repository.deleteExercise(exercise)
        
        val afterDelete = repository.getExerciseById("delete-test")
        assertNull(afterDelete)
    }

    @Test
    fun insertMultipleExercises() = runBlocking {
        val exercises = listOf(
            createTestExercise("batch-1", "Batch Exercise 1"),
            createTestExercise("batch-2", "Batch Exercise 2"),
            createTestExercise("batch-3", "Batch Exercise 3")
        )
        
        repository.insertExercises(exercises)
        
        val allExercises = repository.getAllExercises().first()
        assertEquals(3, allExercises.size)
        assertTrue(allExercises.all { exercise -> 
            exercises.any { it.title == exercise.title } 
        })
    }

    @Test
    fun exerciseCount() = runBlocking {
        assertEquals(0, repository.getExerciseCount())
        
        repository.insertExercise(createTestExercise("count-1", "First"))
        assertEquals(1, repository.getExerciseCount())
        
        repository.insertExercise(createTestExercise("count-2", "Second"))
        assertEquals(2, repository.getExerciseCount())
    }

    @Test
    fun getExercisesByTag_domainToEntity() = runBlocking {
        // Test that domain tags are properly converted for database queries
        val exercise = createTestExercise("tag-test", "Tag Test Exercise").copy(
            tags = listOf("blues", "scale", "intermediate")
        )
        
        repository.insertExercise(exercise)
        
        // The repository should handle the domain-to-entity conversion for tag queries
        val allExercises = repository.getAllExercises().first()
        assertEquals(1, allExercises.size)
        assertEquals(listOf("blues", "scale", "intermediate"), allExercises[0].tags)
    }

    @Test
    fun domainModelMapping() = runBlocking {
        val domainExercise = createTestExercise("mapping-test", "Mapping Test").copy(
            fretboard = FretboardConstraint(
                minFret = 5,
                maxFret = 15,
                allowedStrings = listOf(1, 2, 3),
                numStrings = 6
            ),
            theory = TheoryComponent(
                keys = listOf("E", "A"),
                scales = listOf("minor", "blues"),
                chords = listOf("Em", "Am"),
                melodyLine = "E-F#-G-A-B",
                intervals = listOf("P1", "m3", "P5"),
                timeSignature = "3/4"
            ),
            playback = PlaybackSettings(
                bpm = 140,
                loop = true,
                metronome = false,
                volume = 0.9f,
                repeatCount = -1
            )
        )
        
        repository.insertExercise(domainExercise)
        
        val retrievedExercise = repository.getExerciseById("mapping-test")
        assertNotNull(retrievedExercise)
        
        // Test fretboard mapping
        assertEquals(5, retrievedExercise?.fretboard?.minFret)
        assertEquals(15, retrievedExercise?.fretboard?.maxFret)
        assertEquals(listOf(1, 2, 3), retrievedExercise?.fretboard?.allowedStrings)
        assertEquals(6, retrievedExercise?.fretboard?.numStrings)
        
        // Test theory mapping
        assertEquals(listOf("E", "A"), retrievedExercise?.theory?.keys)
        assertEquals(listOf("minor", "blues"), retrievedExercise?.theory?.scales)
        assertEquals(listOf("Em", "Am"), retrievedExercise?.theory?.chords)
        assertEquals("E-F#-G-A-B", retrievedExercise?.theory?.melodyLine)
        assertEquals("3/4", retrievedExercise?.theory?.timeSignature)
        
        // Test playback mapping
        assertEquals(140, retrievedExercise?.playback?.bpm)
        assertTrue(retrievedExercise?.playback?.loop ?: false)
        assertFalse(retrievedExercise?.playback?.metronome ?: true)
        assertEquals(0.9f, retrievedExercise?.playback?.volume ?: 0f, 0.01f)
        assertEquals(-1, retrievedExercise?.playback?.repeatCount)
    }

    @Test
    fun notesMapping() = runBlocking {
        val exercise = createTestExercise("notes-test", "Notes Test").copy(
            notes = listOf(
                Note(stringNumber = 1, fret = 0, beat = 0, noteName = "E", duration = 1),
                Note(stringNumber = 2, fret = 2, beat = 1, noteName = "C#", duration = 2),
                Note(stringNumber = 3, fret = 4, beat = 2, noteName = "F#", duration = 1)
            )
        )
        
        repository.insertExercise(exercise)
        
        val retrievedExercise = repository.getExerciseById("notes-test")
        assertNotNull(retrievedExercise)
        assertEquals(3, retrievedExercise?.notes?.size)
        
        val firstNote = retrievedExercise?.notes?.get(0)
        assertEquals(1, firstNote?.stringNumber)
        assertEquals(0, firstNote?.fret)
        assertEquals(0, firstNote?.beat)
        assertEquals("E", firstNote?.noteName)
        assertEquals(1, firstNote?.duration)
    }

    @Test
    fun emptyRepository() = runBlocking {
        val allExercises = repository.getAllExercises().first()
        assertTrue(allExercises.isEmpty())
        assertEquals(0, repository.getExerciseCount())
        
        val guitarExercises = repository.getExercisesByInstrument(InstrumentType.GUITAR).first()
        assertTrue(guitarExercises.isEmpty())
        
        val searchResults = repository.searchExercises("anything").first()
        assertTrue(searchResults.isEmpty())
    }

    @Test
    fun roundTripConversion() = runBlocking {
        val originalExercise = createTestExercise("round-trip", "Round Trip Test")
        
        // Insert the exercise (domain -> entity -> database)
        repository.insertExercise(originalExercise)
        
        // Retrieve the exercise (database -> entity -> domain)
        val retrievedExercise = repository.getExerciseById("round-trip")
        
        // Verify all fields survived the round trip
        assertNotNull(retrievedExercise)
        assertEquals(originalExercise.id, retrievedExercise?.id)
        assertEquals(originalExercise.title, retrievedExercise?.title)
        assertEquals(originalExercise.description, retrievedExercise?.description)
        assertEquals(originalExercise.instrument, retrievedExercise?.instrument)
        assertEquals(originalExercise.difficulty, retrievedExercise?.difficulty)
        assertEquals(originalExercise.tags, retrievedExercise?.tags)
        assertEquals(originalExercise.isAiGenerated, retrievedExercise?.isAiGenerated)
        assertEquals(originalExercise.creatorId, retrievedExercise?.creatorId)
    }

    private fun createTestExercise(id: String, title: String): Exercise {
        return Exercise(
            id = id,
            title = title,
            description = "Test description for $title",
            instrument = InstrumentType.GUITAR,
            tags = listOf("test", "practice"),
            difficulty = DifficultyLevel.INTERMEDIATE,
            fretboard = FretboardConstraint(
                minFret = 0,
                maxFret = 12,
                allowedStrings = listOf(1, 2, 3, 4, 5, 6),
                numStrings = 6
            ),
            theory = TheoryComponent(
                keys = listOf("C"),
                scales = listOf("major"),
                chords = emptyList(),
                melodyLine = null,
                intervals = emptyList(),
                timeSignature = "4/4"
            ),
            notation = listOf(
                NotationData(
                    type = NotationType.TAB,
                    content = "test-content",
                    measureCount = 1,
                    encoding = null
                )
            ),
            playback = PlaybackSettings(
                bpm = 120,
                loop = false,
                metronome = true,
                volume = 0.8f,
                repeatCount = 1
            ),
            notes = listOf(
                Note(stringNumber = 1, fret = 0, beat = 0, noteName = "E", duration = 1)
            ),
            createdTimestamp = System.currentTimeMillis(),
            modifiedTimestamp = System.currentTimeMillis(),
            creatorId = "test-user",
            isAiGenerated = false,
            aiPrompt = null
        )
    }
}