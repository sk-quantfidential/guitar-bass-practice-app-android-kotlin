package com.quantfidential.guitarbasspractice.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val instrument: String,
    val tags: String, // JSON string
    val difficulty: String,
    val fretboardConstraint: String, // JSON string
    val theoryComponent: String, // JSON string
    val notation: String, // JSON string
    val playbackSettings: String, // JSON string
    val notes: String, // JSON string
    val createdTimestamp: Long,
    val modifiedTimestamp: Long,
    val creatorId: String,
    val isAiGenerated: Boolean,
    val aiPrompt: String?
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
    val type: String,
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
    GUITAR, BASS, UKULELE, MANDOLIN, BANJO
}

enum class NotationType {
    TAB, STAVE, CHORD_CHART, FRETBOARD
}

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = Gson().toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromIntList(value: List<Int>): String = Gson().toJson(value)

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromFretboardConstraint(value: FretboardConstraint): String = Gson().toJson(value)

    @TypeConverter
    fun toFretboardConstraint(value: String): FretboardConstraint = Gson().fromJson(value, FretboardConstraint::class.java)

    @TypeConverter
    fun fromTheoryComponent(value: TheoryComponent): String = Gson().toJson(value)

    @TypeConverter
    fun toTheoryComponent(value: String): TheoryComponent = Gson().fromJson(value, TheoryComponent::class.java)

    @TypeConverter
    fun fromNotationDataList(value: List<NotationData>): String = Gson().toJson(value)

    @TypeConverter
    fun toNotationDataList(value: String): List<NotationData> {
        val listType = object : TypeToken<List<NotationData>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromPlaybackSettings(value: PlaybackSettings): String = Gson().toJson(value)

    @TypeConverter
    fun toPlaybackSettings(value: String): PlaybackSettings = Gson().fromJson(value, PlaybackSettings::class.java)

    @TypeConverter
    fun fromNoteList(value: List<Note>): String = Gson().toJson(value)

    @TypeConverter
    fun toNoteList(value: String): List<Note> {
        val listType = object : TypeToken<List<Note>>() {}.type
        return Gson().fromJson(value, listType)
    }
}