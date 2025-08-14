package com.quantfidential.guitarbasspractice.util

import com.quantfidential.guitarbasspractice.data.model.*
import com.quantfidential.guitarbasspractice.domain.model.*
import com.quantfidential.guitarbasspractice.domain.model.InstrumentType as DomainInstrumentType
import com.quantfidential.guitarbasspractice.domain.model.DifficultyLevel as DomainDifficultyLevel
import com.quantfidential.guitarbasspractice.domain.model.NotationType as DomainNotationType
import org.junit.Assert.*
import org.junit.Test

class MappersTest {

    @Test
    fun testUserProfileEntity_toDomain() {
        val entity = UserProfileEntity(
            id = "test-id",
            name = "John Doe",
            primaryInstrument = "GUITAR",
            skillLevel = "INTERMEDIATE",
            preferredKeys = """["C", "G", "D"]""",
            favoriteGenres = """["Rock", "Blues"]""",
            createdTimestamp = 123456789L,
            isActive = true
        )
        
        val domain = entity.toDomain()
        
        assertEquals("test-id", domain.id)
        assertEquals("John Doe", domain.name)
        assertEquals(DomainInstrumentType.GUITAR, domain.primaryInstrument)
        assertEquals(DomainDifficultyLevel.INTERMEDIATE, domain.skillLevel)
        assertEquals(listOf("C", "G", "D"), domain.preferredKeys)
        assertEquals(listOf("Rock", "Blues"), domain.favoriteGenres)
        assertEquals(123456789L, domain.createdTimestamp)
        assertTrue(domain.isActive)
    }

    @Test
    fun testUserProfile_toEntity() {
        val domain = UserProfile(
            id = "test-id",
            name = "Jane Smith",
            primaryInstrument = DomainInstrumentType.BASS,
            skillLevel = DomainDifficultyLevel.ADVANCED,
            preferredKeys = listOf("E", "A", "B"),
            favoriteGenres = listOf("Jazz", "Funk"),
            createdTimestamp = 987654321L,
            isActive = false
        )
        
        val entity = domain.toEntity()
        
        assertEquals("test-id", entity.id)
        assertEquals("Jane Smith", entity.name)
        assertEquals("BASS", entity.primaryInstrument)
        assertEquals("ADVANCED", entity.skillLevel)
        assertTrue(entity.preferredKeys.contains("E"))
        assertTrue(entity.preferredKeys.contains("A"))
        assertTrue(entity.preferredKeys.contains("B"))
        assertTrue(entity.favoriteGenres.contains("Jazz"))
        assertTrue(entity.favoriteGenres.contains("Funk"))
        assertEquals(987654321L, entity.createdTimestamp)
        assertFalse(entity.isActive)
    }

    @Test
    fun testExerciseProgressEntity_toDomain() {
        val entity = ExerciseProgressEntity(
            id = "progress-id",
            exerciseId = "exercise-id",
            userId = "user-id",
            completionPercentage = 85,
            bestAccuracy = 92,
            practiceTimeSeconds = 3600,
            lastPracticed = 123456789L,
            practiceCount = 15,
            mistakeFrets = """[3, 7, 12]"""
        )
        
        val domain = entity.toDomain()
        
        assertEquals("progress-id", domain.id)
        assertEquals("exercise-id", domain.exerciseId)
        assertEquals("user-id", domain.userId)
        assertEquals(85, domain.completionPercentage)
        assertEquals(92, domain.bestAccuracy)
        assertEquals(3600, domain.practiceTimeSeconds)
        assertEquals(123456789L, domain.lastPracticed)
        assertEquals(15, domain.practiceCount)
        assertEquals(listOf(3, 7, 12), domain.mistakeFrets)
    }

    @Test
    fun testExerciseProgress_toEntity() {
        val domain = ExerciseProgress(
            id = "progress-id",
            exerciseId = "exercise-id",
            userId = "user-id",
            completionPercentage = 75,
            bestAccuracy = 88,
            practiceTimeSeconds = 2400,
            lastPracticed = 987654321L,
            practiceCount = 10,
            mistakeFrets = listOf(5, 9, 15)
        )
        
        val entity = domain.toEntity()
        
        assertEquals("progress-id", entity.id)
        assertEquals("exercise-id", entity.exerciseId)
        assertEquals("user-id", entity.userId)
        assertEquals(75, entity.completionPercentage)
        assertEquals(88, entity.bestAccuracy)
        assertEquals(2400, entity.practiceTimeSeconds)
        assertEquals(987654321L, entity.lastPracticed)
        assertEquals(10, entity.practiceCount)
        assertTrue(entity.mistakeFrets.contains("5"))
        assertTrue(entity.mistakeFrets.contains("9"))
        assertTrue(entity.mistakeFrets.contains("15"))
    }

    @Test
    fun testDomainToDataModel_FretboardConstraint() {
        val domain = com.quantfidential.guitarbasspractice.domain.model.FretboardConstraint(
            minFret = 0,
            maxFret = 12,
            allowedStrings = listOf(1, 2, 3, 4, 5, 6),
            numStrings = 6
        )
        
        val dataModel = domain.toDataModel()
        
        assertEquals(0, dataModel.minFret)
        assertEquals(12, dataModel.maxFret)
        assertEquals(listOf(1, 2, 3, 4, 5, 6), dataModel.allowedStrings)
        assertEquals(6, dataModel.numStrings)
    }

    @Test
    fun testDomainToDataModel_TheoryComponent() {
        val domain = com.quantfidential.guitarbasspractice.domain.model.TheoryComponent(
            keys = listOf("C", "G", "D"),
            scales = listOf("major", "minor"),
            chords = listOf("C", "Am", "F", "G"),
            melodyLine = "C-D-E-F-G",
            intervals = listOf("P1", "M3", "P5"),
            timeSignature = "4/4"
        )
        
        val dataModel = domain.toDataModel()
        
        assertEquals(listOf("C", "G", "D"), dataModel.keys)
        assertEquals(listOf("major", "minor"), dataModel.scales)
        assertEquals(listOf("C", "Am", "F", "G"), dataModel.chords)
        assertEquals("C-D-E-F-G", dataModel.melodyLine)
        assertEquals(listOf("P1", "M3", "P5"), dataModel.intervals)
        assertEquals("4/4", dataModel.timeSignature)
    }

    @Test
    fun testDomainToDataModel_NotationData() {
        val domain = com.quantfidential.guitarbasspractice.domain.model.NotationData(
            type = DomainNotationType.TAB,
            content = "tab-content",
            measureCount = 4,
            encoding = "utf-8"
        )
        
        val dataModel = domain.toDataModel()
        
        assertEquals("TAB", dataModel.type)
        assertEquals("tab-content", dataModel.content)
        assertEquals(4, dataModel.measureCount)
        assertEquals("utf-8", dataModel.encoding)
    }

    @Test
    fun testDomainToDataModel_PlaybackSettings() {
        val domain = com.quantfidential.guitarbasspractice.domain.model.PlaybackSettings(
            bpm = 120,
            loop = true,
            metronome = false,
            volume = 0.8f,
            repeatCount = 3
        )
        
        val dataModel = domain.toDataModel()
        
        assertEquals(120, dataModel.bpm)
        assertTrue(dataModel.loop)
        assertFalse(dataModel.metronome)
        assertEquals(0.8f, dataModel.volume, 0.01f)
        assertEquals(3, dataModel.repeatCount)
    }

    @Test
    fun testDomainToDataModel_Note() {
        val domain = com.quantfidential.guitarbasspractice.domain.model.Note(
            stringNumber = 1,
            fret = 3,
            beat = 2,
            noteName = "G",
            duration = 1
        )
        
        val dataModel = domain.toDataModel()
        
        assertEquals(1, dataModel.stringNumber)
        assertEquals(3, dataModel.fret)
        assertEquals(2, dataModel.beat)
        assertEquals("G", dataModel.noteName)
        assertEquals(1, dataModel.duration)
    }

    @Test
    fun testRoundTripConversion_UserProfile() {
        val originalDomain = UserProfile(
            id = "round-trip-test",
            name = "Round Trip User",
            primaryInstrument = DomainInstrumentType.GUITAR,
            skillLevel = DomainDifficultyLevel.BEGINNER,
            preferredKeys = listOf("C", "Am", "F", "G"),
            favoriteGenres = listOf("Pop", "Country"),
            createdTimestamp = 1234567890L,
            isActive = true
        )
        
        val convertedDomain = originalDomain.toEntity().toDomain()
        
        assertEquals(originalDomain.id, convertedDomain.id)
        assertEquals(originalDomain.name, convertedDomain.name)
        assertEquals(originalDomain.primaryInstrument, convertedDomain.primaryInstrument)
        assertEquals(originalDomain.skillLevel, convertedDomain.skillLevel)
        assertEquals(originalDomain.preferredKeys, convertedDomain.preferredKeys)
        assertEquals(originalDomain.favoriteGenres, convertedDomain.favoriteGenres)
        assertEquals(originalDomain.createdTimestamp, convertedDomain.createdTimestamp)
        assertEquals(originalDomain.isActive, convertedDomain.isActive)
    }

    @Test
    fun testRoundTripConversion_ExerciseProgress() {
        val originalDomain = ExerciseProgress(
            id = "round-trip-progress",
            exerciseId = "some-exercise",
            userId = "some-user",
            completionPercentage = 67,
            bestAccuracy = 95,
            practiceTimeSeconds = 1800,
            lastPracticed = 9876543210L,
            practiceCount = 25,
            mistakeFrets = listOf(1, 3, 5, 7, 9, 11, 13)
        )
        
        val convertedDomain = originalDomain.toEntity().toDomain()
        
        assertEquals(originalDomain.id, convertedDomain.id)
        assertEquals(originalDomain.exerciseId, convertedDomain.exerciseId)
        assertEquals(originalDomain.userId, convertedDomain.userId)
        assertEquals(originalDomain.completionPercentage, convertedDomain.completionPercentage)
        assertEquals(originalDomain.bestAccuracy, convertedDomain.bestAccuracy)
        assertEquals(originalDomain.practiceTimeSeconds, convertedDomain.practiceTimeSeconds)
        assertEquals(originalDomain.lastPracticed, convertedDomain.lastPracticed)
        assertEquals(originalDomain.practiceCount, convertedDomain.practiceCount)
        assertEquals(originalDomain.mistakeFrets, convertedDomain.mistakeFrets)
    }

    @Test
    fun testEmptyCollections_Handling() {
        val profileWithEmptyLists = UserProfile(
            id = "empty-test",
            name = "Empty User",
            primaryInstrument = DomainInstrumentType.BASS,
            skillLevel = DomainDifficultyLevel.EXPERT,
            preferredKeys = emptyList(),
            favoriteGenres = emptyList(),
            createdTimestamp = 0L,
            isActive = false
        )
        
        val convertedProfile = profileWithEmptyLists.toEntity().toDomain()
        
        assertTrue(convertedProfile.preferredKeys.isEmpty())
        assertTrue(convertedProfile.favoriteGenres.isEmpty())
    }

    @Test
    fun testNullValues_Handling() {
        val progressWithZeros = ExerciseProgress(
            id = "zero-test",
            exerciseId = "zero-exercise",
            userId = "zero-user",
            completionPercentage = 0,
            bestAccuracy = 0,
            practiceTimeSeconds = 0,
            lastPracticed = 0L,
            practiceCount = 0,
            mistakeFrets = emptyList()
        )
        
        val convertedProgress = progressWithZeros.toEntity().toDomain()
        
        assertEquals(0, convertedProgress.completionPercentage)
        assertEquals(0, convertedProgress.bestAccuracy)
        assertEquals(0, convertedProgress.practiceTimeSeconds)
        assertEquals(0L, convertedProgress.lastPracticed)
        assertEquals(0, convertedProgress.practiceCount)
        assertTrue(convertedProgress.mistakeFrets.isEmpty())
    }
}