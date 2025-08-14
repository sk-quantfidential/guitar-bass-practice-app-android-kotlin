package com.quantfidential.guitarbasspractice.di

import com.quantfidential.guitarbasspractice.data.repository.ExerciseRepositoryImpl
import com.quantfidential.guitarbasspractice.data.repository.UserProfileRepositoryImpl
import com.quantfidential.guitarbasspractice.domain.repository.ExerciseRepository
import com.quantfidential.guitarbasspractice.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        exerciseRepositoryImpl: ExerciseRepositoryImpl
    ): ExerciseRepository
    
    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        userProfileRepositoryImpl: UserProfileRepositoryImpl
    ): UserProfileRepository
}