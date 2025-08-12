package com.quantfidential.guitarbasspractice.di

import android.content.Context
import com.quantfidential.guitarbasspractice.data.api.AIComposerApi
import com.quantfidential.guitarbasspractice.util.NetworkConnectivity
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val BASE_URL = "https://api.openai.com/v1/"
    
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideNetworkConnectivity(@ApplicationContext context: Context): NetworkConnectivity {
        return NetworkConnectivity(context)
    }
    
    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    
    @Provides
    @Singleton
    fun provideAIComposerApi(retrofit: Retrofit): AIComposerApi {
        return retrofit.create(AIComposerApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope