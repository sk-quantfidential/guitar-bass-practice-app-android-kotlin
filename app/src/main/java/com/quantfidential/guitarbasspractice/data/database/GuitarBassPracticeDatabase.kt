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

    companion object {
        @Volatile
        private var INSTANCE: GuitarBassPracticeDatabase? = null
        
        private const val DATABASE_NAME = "guitar_bass_practice.db"
        private const val DATABASE_PASSPHRASE = "guitar_bass_practice_secure_key_2024"

        fun getDatabase(context: Context): GuitarBassPracticeDatabase {
            return INSTANCE ?: synchronized(this) {
                val passphrase = SQLiteDatabase.getBytes(DATABASE_PASSPHRASE.toCharArray())
                val factory = SupportFactory(passphrase)
                
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GuitarBassPracticeDatabase::class.java,
                    DATABASE_NAME
                )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration()
                    .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}