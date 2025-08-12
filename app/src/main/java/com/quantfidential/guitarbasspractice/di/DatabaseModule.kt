package com.quantfidential.guitarbasspractice.di

import android.content.Context
import androidx.room.Room
import com.quantfidential.guitarbasspractice.data.database.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    private const val DATABASE_NAME = "guitar_bass_practice_db"
    private const val DATABASE_PASSPHRASE = "guitar_bass_practice_secure_key_2024"
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GuitarBassPracticeDatabase {
        val passphrase = SQLiteDatabase.getBytes(DATABASE_PASSPHRASE.toCharArray())
        val factory = SupportFactory(passphrase)
        
        return Room.databaseBuilder(
            context,
            GuitarBassPracticeDatabase::class.java,
            DATABASE_NAME
        )
        .openHelperFactory(factory)
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun provideExerciseDao(database: GuitarBassPracticeDatabase): ExerciseDao = 
        database.exerciseDao()
    
    @Provides
    fun provideUserProfileDao(database: GuitarBassPracticeDatabase): UserProfileDao = 
        database.userProfileDao()
    
    @Provides
    fun provideExerciseProgressDao(database: GuitarBassPracticeDatabase): ExerciseProgressDao = 
        database.exerciseProgressDao()
}