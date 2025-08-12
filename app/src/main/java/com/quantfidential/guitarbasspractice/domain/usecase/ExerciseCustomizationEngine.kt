package com.quantfidential.guitarbasspractice.domain.usecase

import com.quantfidential.guitarbasspractice.domain.model.*
import com.quantfidential.guitarbasspractice.util.MusicTheoryUtil
import com.quantfidential.guitarbasspractice.util.ScaleType
import com.quantfidential.guitarbasspractice.util.ChordType
import com.quantfidential.guitarbasspractice.util.FretPosition
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

data class CustomizationOptions(
    val instrument: InstrumentType = InstrumentType.GUITAR,
    val difficulty: DifficultyLevel = DifficultyLevel.BEGINNER,
    val minFret: Int = 0,
    val maxFret: Int = 12,
    val allowedStrings: List<Int> = emptyList(),
    val keys: List<String> = listOf("C"),
    val scales: List<String> = listOf("major"),
    val chords: List<String> = emptyList(),
    val bpm: Int = 120,
    val timeSignature: String = "4/4",
    val loop: Boolean = false,
    val metronome: Boolean = true,
    val noteCount: Int = 8,
    val randomize: Boolean = false
)

@Singleton
class ExerciseCustomizationEngine @Inject constructor() {
    
    fun customizeExercise(
        baseExercise: Exercise,
        options: CustomizationOptions
    ): Exercise {
        val customizedNotes = when {
            options.randomize -> generateRandomNotes(options)
            options.scales.isNotEmpty() -> generateScaleExercise(options)
            options.chords.isNotEmpty() -> generateChordExercise(options)
            else -> adaptExistingNotes(baseExercise.notes, options)
        }
        
        return baseExercise.copy(
            instrument = options.instrument,
            difficulty = options.difficulty,
            fretboard = FretboardConstraint(
                minFret = options.minFret,
                maxFret = options.maxFret,
                allowedStrings = options.allowedStrings.ifEmpty { 
                    (1..options.instrument.getDefaultStringCount()).toList() 
                },
                numStrings = options.instrument.getDefaultStringCount()
            ),
            theory = baseExercise.theory.copy(
                keys = options.keys,
                scales = options.scales,
                chords = options.chords,
                timeSignature = options.timeSignature
            ),
            playback = PlaybackSettings(
                bpm = options.bpm,
                loop = options.loop,
                metronome = options.metronome,
                volume = 0.8f,
                repeatCount = if (options.loop) -1 else 1
            ),
            notes = customizedNotes,
            modifiedTimestamp = System.currentTimeMillis()
        )
    }
    
    private fun generateScaleExercise(options: CustomizationOptions): List<Note> {
        val notes = mutableListOf<Note>()
        val tuning = options.instrument.getStandardTuning()
        val key = options.keys.firstOrNull() ?: "C"
        val scaleName = options.scales.firstOrNull() ?: "major"
        val scaleType = ScaleType.values().find { it.name.lowercase().contains(scaleName.lowercase()) } 
                        ?: ScaleType.MAJOR
        
        val scaleNotes = MusicTheoryUtil.getScaleNotes(key, scaleType)
        val positions = MusicTheoryUtil.findNotesOnFretboard(
            tuning, scaleNotes, options.minFret, options.maxFret
        ).filter { options.allowedStrings.isEmpty() || options.allowedStrings.contains(it.string) }
        
        // Generate ascending scale pattern
        val selectedPositions = positions.take(options.noteCount)
        selectedPositions.forEachIndexed { index, position ->
            notes.add(
                Note(
                    stringNumber = position.string,
                    fret = position.fret,
                    beat = index,
                    noteName = position.note,
                    duration = 1
                )
            )
        }
        
        return notes
    }
    
    private fun generateChordExercise(options: CustomizationOptions): List<Note> {
        val notes = mutableListOf<Note>()
        val tuning = options.instrument.getStandardTuning()
        
        options.chords.forEachIndexed { chordIndex, chord ->
            val rootNote = chord.replace(Regex("[^A-G#b]"), "")
            val chordType = when {
                chord.contains("m7") -> ChordType.MINOR7
                chord.contains("7") -> ChordType.DOMINANT7
                chord.contains("maj7") -> ChordType.MAJOR7
                chord.contains("m") -> ChordType.MINOR
                else -> ChordType.MAJOR
            }
            
            val chordNotes = MusicTheoryUtil.getChordNotes(rootNote, chordType)
            val positions = MusicTheoryUtil.findNotesOnFretboard(
                tuning, chordNotes, options.minFret, options.maxFret
            ).filter { options.allowedStrings.isEmpty() || options.allowedStrings.contains(it.string) }
            
            // Select one position per string for chord
            val chordPositions = mutableListOf<FretPosition>()
            for (string in 1..options.instrument.getDefaultStringCount()) {
                positions.find { it.string == string }?.let { chordPositions.add(it) }
            }
            
            chordPositions.forEach { position ->
                notes.add(
                    Note(
                        stringNumber = position.string,
                        fret = position.fret,
                        beat = chordIndex * 4,
                        noteName = position.note,
                        duration = 4
                    )
                )
            }
        }
        
        return notes
    }
    
    private fun generateRandomNotes(options: CustomizationOptions): List<Note> {
        val notes = mutableListOf<Note>()
        val allowedStrings = options.allowedStrings.ifEmpty { 
            (1..options.instrument.getDefaultStringCount()).toList() 
        }
        val tuning = options.instrument.getStandardTuning()
        
        repeat(options.noteCount) { index ->
            val string = allowedStrings.random()
            val fret = Random.nextInt(options.minFret, options.maxFret + 1)
            val noteName = MusicTheoryUtil.getNoteFromFret(tuning[string - 1], fret)
            
            notes.add(
                Note(
                    stringNumber = string,
                    fret = fret,
                    beat = index,
                    noteName = noteName,
                    duration = 1
                )
            )
        }
        
        return notes
    }
    
    private fun adaptExistingNotes(existingNotes: List<Note>, options: CustomizationOptions): List<Note> {
        return existingNotes.mapNotNull { note ->
            // Filter notes that don't fit constraints
            if (note.fret < options.minFret || note.fret > options.maxFret) return@mapNotNull null
            if (options.allowedStrings.isNotEmpty() && !options.allowedStrings.contains(note.stringNumber)) return@mapNotNull null
            
            note.copy()
        }
    }
    
    fun generateExerciseFromTemplate(
        template: ExerciseTemplate,
        options: CustomizationOptions
    ): Exercise {
        val notes = when (template.type) {
            ExerciseType.SCALE_PRACTICE -> generateScaleExercise(options)
            ExerciseType.CHORD_PROGRESSION -> generateChordExercise(options)
            ExerciseType.ARPEGGIO_PRACTICE -> generateArpeggioExercise(options)
            ExerciseType.SIGHT_READING -> generateSightReadingExercise(options)
            ExerciseType.CUSTOM -> generateRandomNotes(options)
        }
        
        return Exercise(
            id = java.util.UUID.randomUUID().toString(),
            title = template.title,
            description = template.description,
            instrument = options.instrument,
            tags = template.tags + listOf(options.difficulty.name.lowercase()),
            difficulty = options.difficulty,
            fretboard = FretboardConstraint(
                minFret = options.minFret,
                maxFret = options.maxFret,
                allowedStrings = options.allowedStrings.ifEmpty { 
                    (1..options.instrument.getDefaultStringCount()).toList() 
                },
                numStrings = options.instrument.getDefaultStringCount()
            ),
            theory = TheoryComponent(
                keys = options.keys,
                scales = options.scales,
                chords = options.chords,
                melodyLine = null,
                intervals = emptyList(),
                timeSignature = options.timeSignature
            ),
            notation = listOf(
                NotationData(
                    type = NotationType.TAB,
                    content = "",
                    measureCount = (notes.size / 4) + 1,
                    encoding = null
                )
            ),
            playback = PlaybackSettings(
                bpm = options.bpm,
                loop = options.loop,
                metronome = options.metronome,
                volume = 0.8f,
                repeatCount = if (options.loop) -1 else 1
            ),
            notes = notes,
            createdTimestamp = System.currentTimeMillis(),
            modifiedTimestamp = System.currentTimeMillis(),
            creatorId = "system",
            isAiGenerated = false,
            aiPrompt = null
        )
    }
    
    private fun generateArpeggioExercise(options: CustomizationOptions): List<Note> {
        val notes = mutableListOf<Note>()
        val key = options.keys.firstOrNull() ?: "C"
        val chordNotes = MusicTheoryUtil.getChordNotes(key, ChordType.MAJOR)
        val tuning = options.instrument.getStandardTuning()
        
        val positions = MusicTheoryUtil.findNotesOnFretboard(
            tuning, chordNotes, options.minFret, options.maxFret
        ).filter { options.allowedStrings.isEmpty() || options.allowedStrings.contains(it.string) }
        
        // Create arpeggio pattern (ascending and descending)
        val pattern = positions.take(4) + positions.take(4).reversed()
        pattern.forEachIndexed { index, position ->
            notes.add(
                Note(
                    stringNumber = position.string,
                    fret = position.fret,
                    beat = index,
                    noteName = position.note,
                    duration = 1
                )
            )
        }
        
        return notes
    }
    
    private fun generateSightReadingExercise(options: CustomizationOptions): List<Note> {
        // Generate random notes within musical constraints
        return generateRandomNotes(options).take(options.noteCount)
    }
}

enum class ExerciseType {
    SCALE_PRACTICE,
    CHORD_PROGRESSION,
    ARPEGGIO_PRACTICE,
    SIGHT_READING,
    CUSTOM
}

data class ExerciseTemplate(
    val type: ExerciseType,
    val title: String,
    val description: String,
    val tags: List<String>
)

object ExerciseTemplates {
    val MAJOR_SCALE = ExerciseTemplate(
        type = ExerciseType.SCALE_PRACTICE,
        title = "Major Scale Practice",
        description = "Practice major scales across the fretboard",
        tags = listOf("scale", "major", "practice")
    )
    
    val MINOR_SCALE = ExerciseTemplate(
        type = ExerciseType.SCALE_PRACTICE,
        title = "Minor Scale Practice",
        description = "Practice natural minor scales",
        tags = listOf("scale", "minor", "practice")
    )
    
    val CHORD_PROGRESSIONS = ExerciseTemplate(
        type = ExerciseType.CHORD_PROGRESSION,
        title = "Chord Progressions",
        description = "Practice common chord progressions",
        tags = listOf("chords", "progression", "harmony")
    )
    
    val ARPEGGIOS = ExerciseTemplate(
        type = ExerciseType.ARPEGGIO_PRACTICE,
        title = "Arpeggio Practice",
        description = "Practice chord arpeggios",
        tags = listOf("arpeggios", "chords", "technique")
    )
    
    val SIGHT_READING = ExerciseTemplate(
        type = ExerciseType.SIGHT_READING,
        title = "Sight Reading",
        description = "Random note reading practice",
        tags = listOf("sight-reading", "notes", "practice")
    )
    
    fun getAllTemplates(): List<ExerciseTemplate> {
        return listOf(MAJOR_SCALE, MINOR_SCALE, CHORD_PROGRESSIONS, ARPEGGIOS, SIGHT_READING)
    }
}