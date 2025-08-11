package com.quantfidential.guitarbasspractice.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quantfidential.guitarbasspractice.data.model.*
import com.quantfidential.guitarbasspractice.domain.model.*

fun ExerciseEntity.toDomain(): Exercise {
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