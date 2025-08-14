package com.quantfidential.guitarbasspractice

import com.quantfidential.guitarbasspractice.domain.model.*
import com.quantfidential.guitarbasspractice.domain.usecase.*
import com.quantfidential.guitarbasspractice.util.MusicTheoryUtil
import com.quantfidential.guitarbasspractice.util.ScaleType
import com.quantfidential.guitarbasspractice.util.ChordType
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for the Guitar & Bass Practice App
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun musicTheoryUtil_getNoteFromFret_isCorrect() {
        // Test standard guitar tuning
        assertEquals("E", MusicTheoryUtil.getNoteFromFret("E", 0))
        assertEquals("F", MusicTheoryUtil.getNoteFromFret("E", 1))
        assertEquals("G", MusicTheoryUtil.getNoteFromFret("E", 3))
        assertEquals("A", MusicTheoryUtil.getNoteFromFret("E", 5))
        
        // Test bass tuning
        assertEquals("E", MusicTheoryUtil.getNoteFromFret("E", 0))
        assertEquals("A", MusicTheoryUtil.getNoteFromFret("A", 0))
        assertEquals("D", MusicTheoryUtil.getNoteFromFret("A", 5))
    }

    @Test
    fun musicTheoryUtil_getScaleNotes_majorScale() {
        val cMajorNotes = MusicTheoryUtil.getScaleNotes("C", ScaleType.MAJOR)
        val expectedNotes = listOf("C", "D", "E", "F", "G", "A", "B")
        assertEquals(expectedNotes, cMajorNotes)
    }

    @Test
    fun musicTheoryUtil_getScaleNotes_minorScale() {
        val aMinorNotes = MusicTheoryUtil.getScaleNotes("A", ScaleType.MINOR)
        val expectedNotes = listOf("A", "B", "C", "D", "E", "F", "G")
        assertEquals(expectedNotes, aMinorNotes)
    }

    @Test
    fun musicTheoryUtil_getChordNotes_majorChord() {
        val cMajorChord = MusicTheoryUtil.getChordNotes("C", ChordType.MAJOR)
        val expectedNotes = listOf("C", "E", "G")
        assertEquals(expectedNotes, cMajorChord)
    }

    @Test
    fun musicTheoryUtil_getChordNotes_minorChord() {
        val aMinorChord = MusicTheoryUtil.getChordNotes("A", ChordType.MINOR)
        val expectedNotes = listOf("A", "C", "E")
        assertEquals(expectedNotes, aMinorChord)
    }

    @Test
    fun instrumentType_getDefaultStringCount_isCorrect() {
        assertEquals(6, InstrumentType.GUITAR.getDefaultStringCount())
        assertEquals(4, InstrumentType.BASS.getDefaultStringCount())
        assertEquals(4, InstrumentType.UKULELE.getDefaultStringCount())
        assertEquals(8, InstrumentType.MANDOLIN.getDefaultStringCount())
        assertEquals(5, InstrumentType.BANJO.getDefaultStringCount())
    }

    @Test
    fun instrumentType_getStandardTuning_guitar() {
        val guitarTuning = InstrumentType.GUITAR.getStandardTuning()
        val expected = listOf("E", "A", "D", "G", "B", "E")
        assertEquals(expected, guitarTuning)
    }

    @Test
    fun instrumentType_getStandardTuning_bass() {
        val bassTuning = InstrumentType.BASS.getStandardTuning()
        val expected = listOf("E", "A", "D", "G")
        assertEquals(expected, bassTuning)
    }

    @Test
    fun exercise_creation_withValidData() {
        val exercise = Exercise(
            id = "test-exercise-1",
            title = "Test Exercise",
            description = "A test exercise for unit testing",
            instrument = InstrumentType.GUITAR,
            tags = listOf("test", "unit"),
            difficulty = DifficultyLevel.BEGINNER,
            fretboard = FretboardConstraint(
                minFret = 0,
                maxFret = 5,
                allowedStrings = listOf(1, 2, 3),
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
                    content = "0-2-3",
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
                Note(1, 0, 0, "E", 1),
                Note(1, 2, 1, "F#", 1),
                Note(1, 3, 2, "G", 1)
            ),
            createdTimestamp = System.currentTimeMillis(),
            modifiedTimestamp = System.currentTimeMillis(),
            creatorId = "test-user",
            isAiGenerated = false,
            aiPrompt = null
        )

        assertEquals("test-exercise-1", exercise.id)
        assertEquals("Test Exercise", exercise.title)
        assertEquals(InstrumentType.GUITAR, exercise.instrument)
        assertEquals(DifficultyLevel.BEGINNER, exercise.difficulty)
        assertEquals(3, exercise.notes.size)
        assertFalse(exercise.isAiGenerated)
    }

    @Test
    fun customizationOptions_defaultValues() {
        val options = CustomizationOptions()
        
        assertEquals(InstrumentType.GUITAR, options.instrument)
        assertEquals(DifficultyLevel.BEGINNER, options.difficulty)
        assertEquals(0, options.minFret)
        assertEquals(12, options.maxFret)
        assertEquals(listOf("C"), options.keys)
        assertEquals(listOf("major"), options.scales)
        assertEquals(120, options.bpm)
        assertEquals("4/4", options.timeSignature)
        assertFalse(options.loop)
        assertTrue(options.metronome)
        assertEquals(8, options.noteCount)
        assertFalse(options.randomize)
    }

    @Test
    fun userProfile_creation() {
        val profile = UserProfile(
            id = "test-profile-1",
            name = "Test User",
            primaryInstrument = InstrumentType.BASS,
            skillLevel = DifficultyLevel.INTERMEDIATE,
            preferredKeys = listOf("E", "A", "D"),
            favoriteGenres = listOf("Rock", "Jazz"),
            createdTimestamp = System.currentTimeMillis(),
            isActive = true
        )

        assertEquals("test-profile-1", profile.id)
        assertEquals("Test User", profile.name)
        assertEquals(InstrumentType.BASS, profile.primaryInstrument)
        assertEquals(DifficultyLevel.INTERMEDIATE, profile.skillLevel)
        assertEquals(3, profile.preferredKeys.size)
        assertEquals(2, profile.favoriteGenres.size)
        assertTrue(profile.isActive)
    }

    @Test
    fun exercisePlaybackState_initialState() {
        val playbackState = ExercisePlaybackState()
        
        assertFalse(playbackState.isPlaying)
        assertEquals(0f, playbackState.currentBeat, 0.001f)
        assertEquals(0, playbackState.currentNoteIndex)
        assertEquals(0f, playbackState.progress, 0.001f)
        assertTrue(playbackState.highlightedPositions.isEmpty())
        assertEquals(120, playbackState.bpm)
        assertFalse(playbackState.loop)
        assertFalse(playbackState.metronome)
    }

    @Test
    fun aiPromptContext_defaultValues() {
        val context = AIPromptContext()
        
        assertEquals(InstrumentType.GUITAR, context.instrument)
        assertEquals(DifficultyLevel.BEGINNER, context.difficulty)
        assertEquals("C", context.key)
        assertEquals("general", context.genre)
        assertEquals("practice", context.exerciseType)
        assertEquals("", context.constraints)
    }

    @Test
    fun fretboardConstraint_validation() {
        val constraint = FretboardConstraint(
            minFret = 3,
            maxFret = 7,
            allowedStrings = listOf(1, 2, 3, 4),
            numStrings = 6
        )
        
        assertTrue(constraint.minFret <= constraint.maxFret)
        assertTrue(constraint.allowedStrings.all { it <= constraint.numStrings })
        assertTrue(constraint.allowedStrings.all { it > 0 })
    }

    @Test
    fun difficultyLevel_ordering() {
        val levels = listOf(
            DifficultyLevel.EXPERT,
            DifficultyLevel.BEGINNER,
            DifficultyLevel.ADVANCED,
            DifficultyLevel.INTERMEDIATE
        )
        
        val sortedLevels = levels.sorted()
        val expected = listOf(
            DifficultyLevel.BEGINNER,
            DifficultyLevel.INTERMEDIATE,
            DifficultyLevel.ADVANCED,
            DifficultyLevel.EXPERT
        )
        
        // Note: This tests alphabetical sorting, not difficulty ordering
        assertEquals(expected, sortedLevels)
    }

    @Test
    fun notationType_allTypesPresent() {
        val types = NotationType.values()
        
        assertTrue(types.contains(NotationType.TAB))
        assertTrue(types.contains(NotationType.STAVE))
        assertTrue(types.contains(NotationType.CHORD_CHART))
        assertTrue(types.contains(NotationType.FRETBOARD))
        assertEquals(4, types.size)
    }
}