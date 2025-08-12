package com.quantfidential.guitarbasspractice.di

import android.content.Context
import androidx.room.Room
import com.quantfidential.guitarbasspractice.data.database.*
import com.quantfidential.guitarbasspractice.util.SecurityUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    private const val DATABASE_NAME = "guitar_bass_practice_db"
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        securityUtil: SecurityUtil
    ): GuitarBassPracticeDatabase {
        val passphrase = securityUtil.getSQLitePassphrase()
        val factory = SupportFactory(passphrase)
        
        return Room.databaseBuilder(
            context,
            GuitarBassPracticeDatabase::class.java,
            DATABASE_NAME
        )
        .openHelperFactory(factory)
        .addMigrations(/* TODO: Add proper migrations instead of destructive fallback */)
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