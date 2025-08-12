package com.quantfidential.guitarbasspractice.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AIComposerApi {
    
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun generateExercise(@Body request: AIExerciseRequest): Response<AIExerciseResponse>
}

data class AIExerciseRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<ChatMessage>,
    val max_tokens: Int = 1000,
    val temperature: Double = 0.7
)

data class ChatMessage(
    val role: String,
    val content: String
)

data class AIExerciseResponse(
    val choices: List<Choice>,
    val usage: Usage? = null
)

data class Choice(
    val message: ChatMessage,
    val finish_reason: String? = null
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

data class GeneratedExerciseData(
    val title: String,
    val description: String,
    val instrument: String,
    val difficulty: String,
    val key: String,
    val scale: String? = null,
    val chords: List<String>? = null,
    val notes: List<GeneratedNote>,
    val bpm: Int,
    val tags: List<String>
)

data class GeneratedNote(
    val string: Int,
    val fret: Int,
    val beat: Float,
    val duration: Float = 1f,
    val note_name: String
)