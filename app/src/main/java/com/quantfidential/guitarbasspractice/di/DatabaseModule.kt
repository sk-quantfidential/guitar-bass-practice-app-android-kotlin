package com.quantfidential.guitarbasspractice.di

import android.content.Context
import com.quantfidential.guitarbasspractice.data.database.*
import com.quantfidential.guitarbasspractice.data.repository.*
import com.quantfidential.guitarbasspractice.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GuitarBassPracticeDatabase {
        return GuitarBassPracticeDatabase.getDatabase(context)
    }

    @Provides
    fun provideExerciseDao(database: GuitarBassPracticeDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    @Provides
    fun provideUserProfileDao(database: GuitarBassPracticeDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    fun provideExerciseProgressDao(database: GuitarBassPracticeDatabase): ExerciseProgressDao {
        return database.exerciseProgressDao()
    }

    @Provides
    @Singleton
    fun provideExerciseRepository(exerciseDao: ExerciseDao): ExerciseRepository {
        return ExerciseRepositoryImpl(exerciseDao)
    }

    @Provides
    @Singleton
    fun provideUserProfileRepository(userProfileDao: UserProfileDao): UserProfileRepository {
        return UserProfileRepositoryImpl(userProfileDao)
    }

    @Provides
    @Singleton
    fun provideExerciseProgressRepository(exerciseProgressDao: ExerciseProgressDao): ExerciseProgressRepository {
        return ExerciseProgressRepositoryImpl(exerciseProgressDao)
    }
}