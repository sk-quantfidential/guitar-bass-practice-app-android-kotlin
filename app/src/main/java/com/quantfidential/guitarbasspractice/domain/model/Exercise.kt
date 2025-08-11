package com.quantfidential.guitarbasspractice.domain.model

data class Exercise(
    val id: String,
    val title: String,
    val description: String,
    val instrument: InstrumentType,
    val tags: List<String>,
    val difficulty: DifficultyLevel,
    val fretboard: FretboardConstraint,
    val theory: TheoryComponent,
    val notation: List<NotationData>,
    val playback: PlaybackSettings,
    val notes: List<Note>,
    val createdTimestamp: Long,
    val modifiedTimestamp: Long,
    val creatorId: String,
    val isAiGenerated: Boolean,
    val aiPrompt: String?
)

data class UserProfile(
    val id: String,
    val name: String,
    val primaryInstrument: InstrumentType,
    val skillLevel: DifficultyLevel,
    val preferredKeys: List<String>,
    val favoriteGenres: List<String>,
    val createdTimestamp: Long,
    val isActive: Boolean
)

data class ExerciseProgress(
    val id: String,
    val exerciseId: String,
    val userId: String,
    val completionPercentage: Int,
    val bestAccuracy: Int,
    val practiceTimeSeconds: Int,
    val lastPracticed: Long,
    val practiceCount: Int,
    val mistakeFrets: List<Int>
)

data class FretboardConstraint(
    val minFret: Int,
    val maxFret: Int,
    val allowedStrings: List<Int>,
    val numStrings: Int
)

data class TheoryComponent(
    val keys: List<String>,
    val scales: List<String>,
    val chords: List<String>,
    val melodyLine: String?,
    val intervals: List<String>,
    val timeSignature: String?
)

data class NotationData(
    val type: NotationType,
    val content: String,
    val measureCount: Int,
    val encoding: String?
)

data class PlaybackSettings(
    val bpm: Int,
    val loop: Boolean,
    val metronome: Boolean,
    val volume: Float,
    val repeatCount: Int
)

data class Note(
    val stringNumber: Int,
    val fret: Int,
    val beat: Int,
    val noteName: String,
    val duration: Int
)

enum class DifficultyLevel {
    BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
}

enum class InstrumentType {
    GUITAR, BASS, UKULELE, MANDOLIN, BANJO;
    
    fun getDefaultStringCount(): Int = when (this) {
        GUITAR -> 6
        BASS -> 4
        UKULELE -> 4
        MANDOLIN -> 8
        BANJO -> 5
    }

    fun getStandardTuning(): List<String> = when (this) {
        GUITAR -> listOf("E", "A", "D", "G", "B", "E")
        BASS -> listOf("E", "A", "D", "G")
        UKULELE -> listOf("G", "C", "E", "A")
        MANDOLIN -> listOf("G", "D", "A", "E", "G", "D", "A", "E")
        BANJO -> listOf("G", "D", "G", "B", "D")
    }
}

enum class NotationType {
    TAB, STAVE, CHORD_CHART, FRETBOARD
}