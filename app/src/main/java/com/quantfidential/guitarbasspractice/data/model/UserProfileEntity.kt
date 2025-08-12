package com.quantfidential.guitarbasspractice.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val primaryInstrument: String,
    val skillLevel: String,
    val preferredKeys: String, // JSON string
    val favoriteGenres: String, // JSON string
    val createdTimestamp: Long,
    val isActive: Boolean
)

@Entity(tableName = "exercise_progress")
data class ExerciseProgressEntity(
    @PrimaryKey val id: String,
    val exerciseId: String,
    val userId: String,
    val completionPercentage: Int,
    val bestAccuracy: Int,
    val practiceTimeSeconds: Int,
    val lastPracticed: Long,
    val practiceCount: Int,
    val mistakeFrets: String // JSON string for List<Int>
)