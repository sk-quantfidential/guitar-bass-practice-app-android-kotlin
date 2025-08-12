package com.quantfidential.guitarbasspractice.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.quantfidential.guitarbasspractice.data.model.Converters
import com.quantfidential.guitarbasspractice.data.model.ExerciseEntity
import com.quantfidential.guitarbasspractice.data.model.UserProfileEntity
import com.quantfidential.guitarbasspractice.data.model.ExerciseProgressEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [
        ExerciseEntity::class,
        UserProfileEntity::class,
        ExerciseProgressEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class GuitarBassPracticeDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun exerciseProgressDao(): ExerciseProgressDao

    // Database instance is now managed by Hilt DI - no companion object needed
}