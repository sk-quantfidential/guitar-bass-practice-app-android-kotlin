package com.quantfidential.guitarbasspractice.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quantfidential.guitarbasspractice.data.model.*
import com.quantfidential.guitarbasspractice.domain.model.*
import com.quantfidential.guitarbasspractice.domain.model.InstrumentType
import com.quantfidential.guitarbasspractice.domain.model.DifficultyLevel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Injectable mapper utility that converts between domain and data models.
 * Uses a single Gson instance for consistency and performance.
 */
@Singleton
class EntityMapper @Inject constructor(
    private val gson: Gson
) {

    fun ExerciseEntity.toDomain(): Exercise = safeCall {
        Exercise(
            id = id,
            title = title,
            description = description,
            instrument = InstrumentType.valueOf(instrument),
            tags = parseJson(tags, object : TypeToken<List<String>>() {}.type),
            difficulty = DifficultyLevel.valueOf(difficulty),
            fretboard = parseJson(fretboardConstraint, com.quantfidential.guitarbasspractice.domain.model.FretboardConstraint::class.java),
            theory = parseJson(theoryComponent, com.quantfidential.guitarbasspractice.domain.model.TheoryComponent::class.java),
            notation = parseJson(notation, object : TypeToken<List<com.quantfidential.guitarbasspractice.domain.model.NotationData>>() {}.type),
            playback = parseJson(playbackSettings, com.quantfidential.guitarbasspractice.domain.model.PlaybackSettings::class.java),
            notes = parseJson(notes, object : TypeToken<List<com.quantfidential.guitarbasspractice.domain.model.Note>>() {}.type),
            createdTimestamp = createdTimestamp,
            modifiedTimestamp = modifiedTimestamp,
            creatorId = creatorId,
            isAiGenerated = isAiGenerated,
            aiPrompt = aiPrompt
        )
    }.getOrDefault(createDefaultExercise())
    
    fun Exercise.toEntity(): ExerciseEntity = safeCall {
        ExerciseEntity(
            id = id,
            title = title,
            description = description,
            instrument = instrument.name,
            tags = toJson(tags),
            difficulty = difficulty.name,
            fretboardConstraint = toJson(fretboard.toDataModel()),
            theoryComponent = toJson(theory.toDataModel()),
            notation = toJson(notation.map { it.toDataModel() }),
            playbackSettings = toJson(playback.toDataModel()),
            notes = toJson(notes.map { it.toDataModel() }),
            createdTimestamp = createdTimestamp,
            modifiedTimestamp = modifiedTimestamp,
            creatorId = creatorId,
            isAiGenerated = isAiGenerated,
            aiPrompt = aiPrompt
        )
    }.getOrDefault(createDefaultExerciseEntity())
    
    private fun <T> parseJson(json: String, type: java.lang.reflect.Type): T {
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            throw AppException.JsonParsingException("Failed to parse JSON: ${e.message}", e)
        }
    }
    
    private fun <T> parseJson(json: String, clazz: Class<T>): T {
        return try {
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            throw AppException.JsonParsingException("Failed to parse JSON: ${e.message}", e)
        }
    }
    
    private fun toJson(obj: Any): String {
        return try {
            gson.toJson(obj)
        } catch (e: Exception) {
            throw AppException.JsonParsingException("Failed to serialize to JSON: ${e.message}", e)
        }
    }
    
    private fun createDefaultExercise(): Exercise {
        return Exercise(
            id = "error",
            title = "Error Loading Exercise",
            description = "Failed to load exercise data",
            instrument = InstrumentType.GUITAR,
            tags = emptyList(),
            difficulty = DifficultyLevel.BEGINNER,
            fretboard = com.quantfidential.guitarbasspractice.domain.model.FretboardConstraint(0, 12, listOf(1,2,3,4,5,6), 6),
            theory = com.quantfidential.guitarbasspractice.domain.model.TheoryComponent(listOf("C"), listOf("major"), emptyList(), null, emptyList(), "4/4"),
            notation = emptyList(),
            playback = com.quantfidential.guitarbasspractice.domain.model.PlaybackSettings(120, false, true, 0.8f, 1),
            notes = emptyList(),
            createdTimestamp = System.currentTimeMillis(),
            modifiedTimestamp = System.currentTimeMillis(),
            creatorId = "system",
            isAiGenerated = false,
            aiPrompt = null
        )
    }
    
    private fun createDefaultExerciseEntity(): ExerciseEntity {
        return ExerciseEntity(
            id = "error",
            title = "Error Saving Exercise",
            description = "Failed to save exercise data",
            instrument = "GUITAR",
            tags = "[]",
            difficulty = "BEGINNER",
            fretboardConstraint = "{}",
            theoryComponent = "{}",
            notation = "[]",
            playbackSettings = "{}",
            notes = "[]",
            createdTimestamp = System.currentTimeMillis(),
            modifiedTimestamp = System.currentTimeMillis(),
            creatorId = "system",
            isAiGenerated = false,
            aiPrompt = null
        )
    }
}

// Extension functions that delegate to legacy mappers
// TODO: Replace these with direct EntityMapper usage throughout the codebase

fun ExerciseEntity.toDomain(): Exercise {
    // This is a temporary bridge function until we refactor all repositories to inject EntityMapper
    val gson = Gson()
    return Exercise(
        id = id,
        title = title,
        description = description,
        instrument = InstrumentType.valueOf(instrument),
        tags = gson.fromJson(tags, object : TypeToken<List<String>>() {}.type),
        difficulty = DifficultyLevel.valueOf(difficulty),
        fretboard = gson.fromJson(fretboardConstraint, com.quantfidential.guitarbasspractice.domain.model.FretboardConstraint::class.java),
        theory = gson.fromJson(theoryComponent, com.quantfidential.guitarbasspractice.domain.model.TheoryComponent::class.java),
        notation = gson.fromJson(notation, object : TypeToken<List<com.quantfidential.guitarbasspractice.domain.model.NotationData>>() {}.type),
        playback = gson.fromJson(playbackSettings, com.quantfidential.guitarbasspractice.domain.model.PlaybackSettings::class.java),
        notes = gson.fromJson(notes, object : TypeToken<List<com.quantfidential.guitarbasspractice.domain.model.Note>>() {}.type),
        createdTimestamp = createdTimestamp,
        modifiedTimestamp = modifiedTimestamp,
        creatorId = creatorId,
        isAiGenerated = isAiGenerated,
        aiPrompt = aiPrompt
    )
}

fun Exercise.toEntity(): ExerciseEntity {
    // This is a temporary bridge function until we refactor all repositories to inject EntityMapper
    val gson = Gson()
    return ExerciseEntity(
        id = id,
        title = title,
        description = description,
        instrument = instrument.name,
        tags = gson.toJson(tags),
        difficulty = difficulty.name,
        fretboardConstraint = gson.toJson(fretboard.toDataModel()),
        theoryComponent = gson.toJson(theory.toDataModel()),
        notation = gson.toJson(notation.map { it.toDataModel() }),
        playbackSettings = gson.toJson(playback.toDataModel()),
        notes = gson.toJson(notes.map { it.toDataModel() }),
        createdTimestamp = createdTimestamp,
        modifiedTimestamp = modifiedTimestamp,
        creatorId = creatorId,
        isAiGenerated = isAiGenerated,
        aiPrompt = aiPrompt
    )
}

fun UserProfileEntity.toDomain(): UserProfile {
    val gson = Gson()
    return UserProfile(
        id = id,
        name = name,
        primaryInstrument = InstrumentType.valueOf(primaryInstrument),
        skillLevel = DifficultyLevel.valueOf(skillLevel),
        preferredKeys = gson.fromJson(preferredKeys, object : TypeToken<List<String>>() {}.type),
        favoriteGenres = gson.fromJson(favoriteGenres, object : TypeToken<List<String>>() {}.type),
        createdTimestamp = createdTimestamp,
        isActive = isActive
    )
}

fun UserProfile.toEntity(): UserProfileEntity {
    val gson = Gson()
    return UserProfileEntity(
        id = id,
        name = name,
        primaryInstrument = primaryInstrument.name,
        skillLevel = skillLevel.name,
        preferredKeys = gson.toJson(preferredKeys),
        favoriteGenres = gson.toJson(favoriteGenres),
        createdTimestamp = createdTimestamp,
        isActive = isActive
    )
}

fun ExerciseProgressEntity.toDomain(): ExerciseProgress {
    val gson = Gson()
    return ExerciseProgress(
        id = id,
        exerciseId = exerciseId,
        userId = userId,
        completionPercentage = completionPercentage,
        bestAccuracy = bestAccuracy,
        practiceTimeSeconds = practiceTimeSeconds,
        lastPracticed = lastPracticed,
        practiceCount = practiceCount,
        mistakeFrets = gson.fromJson(mistakeFrets, object : TypeToken<List<Int>>() {}.type)
    )
}

fun ExerciseProgress.toEntity(): ExerciseProgressEntity {
    val gson = Gson()
    return ExerciseProgressEntity(
        id = id,
        exerciseId = exerciseId,
        userId = userId,
        completionPercentage = completionPercentage,
        bestAccuracy = bestAccuracy,
        practiceTimeSeconds = practiceTimeSeconds,
        lastPracticed = lastPracticed,
        practiceCount = practiceCount,
        mistakeFrets = gson.toJson(mistakeFrets)
    )
}

// Extension functions to convert between domain and data models
fun com.quantfidential.guitarbasspractice.domain.model.FretboardConstraint.toDataModel(): com.quantfidential.guitarbasspractice.data.model.FretboardConstraint {
    return com.quantfidential.guitarbasspractice.data.model.FretboardConstraint(
        minFret = minFret,
        maxFret = maxFret,
        allowedStrings = allowedStrings,
        numStrings = numStrings
    )
}

fun com.quantfidential.guitarbasspractice.domain.model.TheoryComponent.toDataModel(): com.quantfidential.guitarbasspractice.data.model.TheoryComponent {
    return com.quantfidential.guitarbasspractice.data.model.TheoryComponent(
        keys = keys,
        scales = scales,
        chords = chords,
        melodyLine = melodyLine,
        intervals = intervals,
        timeSignature = timeSignature
    )
}

fun com.quantfidential.guitarbasspractice.domain.model.NotationData.toDataModel(): com.quantfidential.guitarbasspractice.data.model.NotationData {
    return com.quantfidential.guitarbasspractice.data.model.NotationData(
        type = type.name,
        content = content,
        measureCount = measureCount,
        encoding = encoding
    )
}

fun com.quantfidential.guitarbasspractice.domain.model.PlaybackSettings.toDataModel(): com.quantfidential.guitarbasspractice.data.model.PlaybackSettings {
    return com.quantfidential.guitarbasspractice.data.model.PlaybackSettings(
        bpm = bpm,
        loop = loop,
        metronome = metronome,
        volume = volume,
        repeatCount = repeatCount
    )
}

fun com.quantfidential.guitarbasspractice.domain.model.Note.toDataModel(): com.quantfidential.guitarbasspractice.data.model.Note {
    return com.quantfidential.guitarbasspractice.data.model.Note(
        stringNumber = stringNumber,
        fret = fret,
        beat = beat,
        noteName = noteName,
        duration = duration
    )
}