package com.quantfidential.guitarbasspractice.domain.usecase

import com.quantfidential.guitarbasspractice.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExerciseCustomizationEngineTest {
    
    private lateinit var customizationEngine: ExerciseCustomizationEngine
    
    @Before
    fun setup() {
        customizationEngine = ExerciseCustomizationEngine()
    }
    
    @Test
    fun testDefaultCustomizationOptions() {
        val options = CustomizationOptions()
        
        assertEquals(InstrumentType.GUITAR, options.instrument)
        assertEquals(DifficultyLevel.BEGINNER, options.difficulty)
        assertEquals(0, options.minFret)
        assertEquals(12, options.maxFret)
        assertTrue(options.allowedStrings.isEmpty())
        assertEquals(listOf("C"), options.keys)
        assertEquals(listOf("major"), options.scales)
        assertTrue(options.chords.isEmpty())
        assertEquals(120, options.bpm)
        assertEquals("4/4", options.timeSignature)
        assertFalse(options.loop)
        assertTrue(options.metronome)
        assertEquals(8, options.noteCount)
        assertFalse(options.randomize)
    }
    
    @Test
    fun testCustomizationOptionsWithValues() {
        val options = CustomizationOptions(
            instrument = InstrumentType.BASS,
            difficulty = DifficultyLevel.ADVANCED,
            minFret = 3,
            maxFret = 15,
            allowedStrings = listOf(1, 2, 3),
            keys = listOf("G", "D"),
            scales = listOf("minor", "blues"),
            chords = listOf("Gm", "D7"),
            bpm = 140,
            timeSignature = "3/4",
            loop = true,
            metronome = false,
            noteCount = 16,
            randomize = true
        )
        
        assertEquals(InstrumentType.BASS, options.instrument)
        assertEquals(DifficultyLevel.ADVANCED, options.difficulty)
        assertEquals(3, options.minFret)
        assertEquals(15, options.maxFret)
        assertEquals(listOf(1, 2, 3), options.allowedStrings)
        assertEquals(listOf("G", "D"), options.keys)
        assertEquals(listOf("minor", "blues"), options.scales)
        assertEquals(listOf("Gm", "D7"), options.chords)
        assertEquals(140, options.bpm)
        assertEquals("3/4", options.timeSignature)
        assertTrue(options.loop)
        assertFalse(options.metronome)
        assertEquals(16, options.noteCount)
        assertTrue(options.randomize)
    }
    
    @Test
    fun testCreateCustomExercise_withScales() {
        val options = CustomizationOptions(
            instrument = InstrumentType.GUITAR,
            difficulty = DifficultyLevel.INTERMEDIATE,
            scales = listOf("major"),
            keys = listOf("C"),
            noteCount = 4
        )
        
        val exercise = customizationEngine.createCustomExercise(options)
        
        assertEquals("Custom GUITAR Exercise", exercise.title)
        assertTrue(exercise.description.contains("intermediate"))
        assertEquals(InstrumentType.GUITAR, exercise.instrument)
        assertEquals(DifficultyLevel.INTERMEDIATE, exercise.difficulty)
        assertTrue(exercise.tags.contains("custom"))
        assertTrue(exercise.tags.contains("intermediate"))
        assertEquals(listOf("C"), exercise.theory.keys)
        assertEquals(listOf("major"), exercise.theory.scales)
        assertFalse(exercise.isAiGenerated)
        assertNull(exercise.aiPrompt)
        assertEquals("user", exercise.creatorId)
        
        // Should have generated notes for the scale
        assertFalse(exercise.notes.isEmpty())
        assertTrue(exercise.notes.size <= options.noteCount)
        
        // Notes should be within fret constraints
        exercise.notes.forEach { note ->
            assertTrue(note.fret >= options.minFret)
            assertTrue(note.fret <= options.maxFret)
        }
    }
    
    @Test
    fun testCreateCustomExercise_withChords() {
        val options = CustomizationOptions(
            scales = emptyList(),
            chords = listOf("C", "Am", "F", "G"),
            keys = listOf("C"),
            noteCount = 4
        )
        
        val exercise = customizationEngine.createCustomExercise(options)
        
        assertEquals(listOf("C", "Am", "F", "G"), exercise.theory.chords)
        assertFalse(exercise.notes.isEmpty())
        
        // Chord exercises should have multiple simultaneous notes (chord positions)
        val firstChordNotes = exercise.notes.filter { it.beat == 0 }
        assertTrue(firstChordNotes.size > 1) // Should be multiple strings for chord
    }
    
    @Test
    fun testCreateCustomExercise_randomized() {
        val options = CustomizationOptions(
            randomize = true,
            noteCount = 6,
            minFret = 5,
            maxFret = 10
        )
        
        val exercise = customizationEngine.createCustomExercise(options)
        
        assertEquals(6, exercise.notes.size)
        
        // All notes should be within constraints
        exercise.notes.forEach { note ->
            assertTrue(note.fret >= 5)
            assertTrue(note.fret <= 10)
            assertTrue(note.stringNumber >= 1)
            assertTrue(note.stringNumber <= 6) // Guitar default
        }
        
        // Notes should be sequential beats
        exercise.notes.forEachIndexed { index, note ->
            assertEquals(index, note.beat)
        }
    }
    
    @Test
    fun testCustomizeExercise() {
        val baseExercise = createTestExercise()
        val options = CustomizationOptions(
            instrument = InstrumentType.BASS,
            difficulty = DifficultyLevel.EXPERT,
            bpm = 180,
            loop = true,
            keys = listOf("E"),
            scales = listOf("blues")
        )
        
        val customizedExercise = customizationEngine.customizeExercise(baseExercise, options)
        
        assertEquals(InstrumentType.BASS, customizedExercise.instrument)
        assertEquals(DifficultyLevel.EXPERT, customizedExercise.difficulty)
        assertEquals(180, customizedExercise.playback.bpm)
        assertTrue(customizedExercise.playback.loop)
        assertEquals(listOf("E"), customizedExercise.theory.keys)
        assertEquals(listOf("blues"), customizedExercise.theory.scales)
        assertTrue(customizedExercise.modifiedTimestamp > baseExercise.modifiedTimestamp)
    }
    
    @Test
    fun testGenerateExerciseFromTemplate_scaleTemplate() {
        val options = CustomizationOptions(
            keys = listOf("G"),
            scales = listOf("major"),
            noteCount = 7
        )
        
        val exercise = customizationEngine.generateExerciseFromTemplate(
            ExerciseTemplates.MAJOR_SCALE,
            options
        )
        
        assertEquals("Major Scale Practice", exercise.title)
        assertEquals("Practice major scales across the fretboard", exercise.description)
        assertTrue(exercise.tags.contains("scale"))
        assertTrue(exercise.tags.contains("major"))
        assertTrue(exercise.tags.contains("practice"))
        assertEquals("system", exercise.creatorId)
        assertFalse(exercise.isAiGenerated)
    }
    
    @Test
    fun testGenerateExerciseFromTemplate_chordTemplate() {
        val options = CustomizationOptions(
            chords = listOf("C", "F", "G"),
            keys = listOf("C")
        )
        
        val exercise = customizationEngine.generateExerciseFromTemplate(
            ExerciseTemplates.CHORD_PROGRESSIONS,
            options
        )
        
        assertEquals("Chord Progressions", exercise.title)
        assertTrue(exercise.tags.contains("chords"))
        assertTrue(exercise.tags.contains("progression"))
        assertEquals(listOf("C", "F", "G"), exercise.theory.chords)
    }
    
    @Test
    fun testExerciseType_enum() {
        val scaleType = ExerciseType.SCALE_PRACTICE
        val chordType = ExerciseType.CHORD_PROGRESSION
        val arpeggioType = ExerciseType.ARPEGGIO_PRACTICE
        val sightReadingType = ExerciseType.SIGHT_READING
        val customType = ExerciseType.CUSTOM
        
        assertEquals("SCALE_PRACTICE", scaleType.name)
        assertEquals("CHORD_PROGRESSION", chordType.name)
        assertEquals("ARPEGGIO_PRACTICE", arpeggioType.name)
        assertEquals("SIGHT_READING", sightReadingType.name)
        assertEquals("CUSTOM", customType.name)
    }
    
    @Test
    fun testExerciseTemplate_dataClass() {
        val template = ExerciseTemplate(
            type = ExerciseType.SCALE_PRACTICE,
            title = "Test Scale",
            description = "Test description",
            tags = listOf("test", "scale")
        )
        
        assertEquals(ExerciseType.SCALE_PRACTICE, template.type)
        assertEquals("Test Scale", template.title)
        assertEquals("Test description", template.description)
        assertEquals(listOf("test", "scale"), template.tags)
    }
    
    @Test
    fun testExerciseTemplates_predefinedTemplates() {
        val allTemplates = ExerciseTemplates.getAllTemplates()
        
        assertEquals(5, allTemplates.size)
        
        val majorScale = ExerciseTemplates.MAJOR_SCALE
        assertEquals(ExerciseType.SCALE_PRACTICE, majorScale.type)
        assertEquals("Major Scale Practice", majorScale.title)
        
        val minorScale = ExerciseTemplates.MINOR_SCALE
        assertEquals(ExerciseType.SCALE_PRACTICE, minorScale.type)
        assertEquals("Minor Scale Practice", minorScale.title)
        
        val chordProgressions = ExerciseTemplates.CHORD_PROGRESSIONS
        assertEquals(ExerciseType.CHORD_PROGRESSION, chordProgressions.type)
        assertEquals("Chord Progressions", chordProgressions.title)
        
        val arpeggios = ExerciseTemplates.ARPEGGIOS
        assertEquals(ExerciseType.ARPEGGIO_PRACTICE, arpeggios.type)
        assertEquals("Arpeggio Practice", arpeggios.title)
        
        val sightReading = ExerciseTemplates.SIGHT_READING
        assertEquals(ExerciseType.SIGHT_READING, sightReading.type)
        assertEquals("Sight Reading", sightReading.title)
    }
    
    @Test
    fun testConstraints_allowedStrings() {
        val options = CustomizationOptions(
            allowedStrings = listOf(1, 2), // Only first two strings
            noteCount = 4,
            randomize = true
        )
        
        val exercise = customizationEngine.createCustomExercise(options)
        
        // All notes should only be on strings 1 or 2
        exercise.notes.forEach { note ->
            assertTrue(note.stringNumber in listOf(1, 2))
        }
    }
    
    @Test
    fun testConstraints_emptyAllowedStrings() {
        val options = CustomizationOptions(
            allowedStrings = emptyList(), // Should default to all strings
            noteCount = 6,
            randomize = true
        )
        
        val exercise = customizationEngine.createCustomExercise(options)
        
        // Should use all 6 guitar strings
        assertEquals(6, exercise.fretboard.numStrings)
        assertEquals((1..6).toList(), exercise.fretboard.allowedStrings)
    }
    
    @Test
    fun testFretboardConstraints() {
        val options = CustomizationOptions(
            minFret = 5,
            maxFret = 8,
            instrument = InstrumentType.BASS
        )
        
        val exercise = customizationEngine.createCustomExercise(options)
        
        assertEquals(5, exercise.fretboard.minFret)
        assertEquals(8, exercise.fretboard.maxFret)
        assertEquals(4, exercise.fretboard.numStrings) // Bass default
    }
    
    @Test
    fun testPlaybackSettings_fromOptions() {
        val options = CustomizationOptions(
            bpm = 90,
            loop = true,
            metronome = false
        )
        
        val exercise = customizationEngine.createCustomExercise(options)
        
        assertEquals(90, exercise.playback.bpm)
        assertTrue(exercise.playback.loop)
        assertFalse(exercise.playback.metronome)
        assertEquals(0.8f, exercise.playback.volume, 0.01f)
        assertEquals(1, exercise.playback.repeatCount) // createCustomExercise always uses 1
    }
    
    @Test
    fun testPlaybackSettings_noLoop() {
        val options = CustomizationOptions(
            loop = false
        )
        
        val exercise = customizationEngine.createCustomExercise(options)
        
        assertEquals(1, exercise.playback.repeatCount) // No loop = single repeat
    }
    
    private fun createTestExercise(): Exercise {
        return Exercise(
            id = "test-exercise",
            title = "Original Exercise",
            description = "Original description",
            instrument = InstrumentType.GUITAR,
            tags = listOf("original"),
            difficulty = DifficultyLevel.BEGINNER,
            fretboard = FretboardConstraint(0, 12, listOf(1, 2, 3, 4, 5, 6), 6),
            theory = TheoryComponent(listOf("C"), listOf("major"), emptyList(), null, emptyList(), "4/4"),
            notation = listOf(NotationData(NotationType.TAB, "", 1, null)),
            playback = PlaybackSettings(120, false, true, 0.8f, 1),
            notes = listOf(Note(1, 0, 0, "E", 1)),
            createdTimestamp = 123456789L,
            modifiedTimestamp = 123456789L,
            creatorId = "original-user",
            isAiGenerated = false,
            aiPrompt = null
        )
    }
}