package com.quantfidential.guitarbasspractice.domain.usecase

import com.google.gson.Gson
import com.quantfidential.guitarbasspractice.data.api.AIComposerApi
import com.quantfidential.guitarbasspractice.data.api.AIExerciseRequest
import com.quantfidential.guitarbasspractice.data.api.ChatMessage
import com.quantfidential.guitarbasspractice.data.api.GeneratedExerciseData
import com.quantfidential.guitarbasspractice.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class AIGenerationResult {
    data class Success(val exercise: Exercise) : AIGenerationResult()
    data class Error(val message: String) : AIGenerationResult()
    object Loading : AIGenerationResult()
}

data class AIPromptContext(
    val instrument: InstrumentType = InstrumentType.GUITAR,
    val difficulty: DifficultyLevel = DifficultyLevel.BEGINNER,
    val key: String = "C",
    val genre: String = "general",
    val exerciseType: String = "practice",
    val constraints: String = ""
)

@Singleton
class AIComposerAgent @Inject constructor(
    private val aiApi: AIComposerApi,
    private val gson: Gson
) {
    
    fun generateExerciseFromPrompt(
        prompt: String,
        context: AIPromptContext
    ): Flow<AIGenerationResult> = flow {
        emit(AIGenerationResult.Loading)
        
        try {
            val systemPrompt = createSystemPrompt(context)
            val userPrompt = createUserPrompt(prompt, context)
            
            val request = AIExerciseRequest(
                messages = listOf(
                    ChatMessage("system", systemPrompt),
                    ChatMessage("user", userPrompt)
                )
            )
            
            val response = aiApi.generateExercise(request)
            
            if (response.isSuccessful && response.body() != null) {
                val aiResponse = response.body()!!
                val generatedContent = aiResponse.choices.firstOrNull()?.message?.content
                
                if (generatedContent != null) {
                    val exercise = parseAIResponse(generatedContent, prompt, context)
                    emit(AIGenerationResult.Success(exercise))
                } else {
                    emit(AIGenerationResult.Error("No content generated"))
                }
            } else {
                emit(AIGenerationResult.Error("API Error: ${response.code()}"))
            }
            
        } catch (e: Exception) {
            emit(AIGenerationResult.Error("Generation failed: ${e.message}"))
        }
    }
    
    private fun createSystemPrompt(context: AIPromptContext): String {
        return """
            You are a professional music instructor and composer specializing in ${context.instrument.name.lowercase()} exercises.
            
            Your task is to generate structured musical exercises based on user prompts. Always respond with valid JSON in the following format:
            
            {
                "title": "Exercise Title",
                "description": "Brief description of the exercise",
                "instrument": "${context.instrument.name}",
                "difficulty": "${context.difficulty.name}",
                "key": "C",
                "scale": "major",
                "chords": ["C", "Am", "F", "G"],
                "notes": [
                    {
                        "string": 1,
                        "fret": 0,
                        "beat": 0.0,
                        "duration": 1.0,
                        "note_name": "E"
                    }
                ],
                "bpm": 120,
                "tags": ["scale", "practice", "beginner"]
            }
            
            Guidelines:
            - For ${context.instrument.name}, use ${context.instrument.getDefaultStringCount()} strings numbered 1-${context.instrument.getDefaultStringCount()}
            - String 1 is the highest pitch string
            - Standard tuning: ${context.instrument.getStandardTuning().joinToString(", ")}
            - Keep exercises appropriate for ${context.difficulty.name} level
            - Notes should be playable on the instrument
            - Beat values represent timing (0.0 = first beat, 1.0 = second beat, etc.)
            - Duration is in beats (1.0 = quarter note, 0.5 = eighth note, etc.)
            
            Focus on creating educational, progressive exercises that help students improve their skills.
        """.trimIndent()
    }
    
    private fun createUserPrompt(prompt: String, context: AIPromptContext): String {
        return """
            Create a ${context.instrument.name.lowercase()} exercise: $prompt
            
            Context:
            - Difficulty: ${context.difficulty.name}
            - Key: ${context.key}
            - Genre: ${context.genre}
            - Type: ${context.exerciseType}
            ${if (context.constraints.isNotEmpty()) "- Constraints: ${context.constraints}" else ""}
            
            Respond only with valid JSON matching the specified format.
        """.trimIndent()
    }
    
    private fun parseAIResponse(content: String, originalPrompt: String, context: AIPromptContext): Exercise {
        return try {
            // Extract JSON from the response (in case there's additional text)
            val jsonStart = content.indexOf('{')
            val jsonEnd = content.lastIndexOf('}')
            val jsonContent = if (jsonStart != -1 && jsonEnd != -1) {
                content.substring(jsonStart, jsonEnd + 1)
            } else {
                content
            }
            
            val generatedData = gson.fromJson(jsonContent, GeneratedExerciseData::class.java)
            convertToExercise(generatedData, originalPrompt)
            
        } catch (e: Exception) {
            // Fallback: create a simple exercise if parsing fails
            createFallbackExercise(originalPrompt, context)
        }
    }
    
    private fun convertToExercise(data: GeneratedExerciseData, originalPrompt: String): Exercise {
        val notes = data.notes.map { generatedNote ->
            Note(
                stringNumber = generatedNote.string,
                fret = generatedNote.fret,
                beat = generatedNote.beat.toInt(),
                noteName = generatedNote.note_name,
                duration = generatedNote.duration.toInt()
            )
        }
        
        return Exercise(
            id = java.util.UUID.randomUUID().toString(),
            title = data.title,
            description = data.description,
            instrument = InstrumentType.valueOf(data.instrument.uppercase()),
            tags = data.tags + listOf("ai-generated"),
            difficulty = DifficultyLevel.valueOf(data.difficulty.uppercase()),
            fretboard = FretboardConstraint(
                minFret = notes.minOfOrNull { it.fret } ?: 0,
                maxFret = notes.maxOfOrNull { it.fret } ?: 12,
                allowedStrings = notes.map { it.stringNumber }.distinct(),
                numStrings = InstrumentType.valueOf(data.instrument.uppercase()).getDefaultStringCount()
            ),
            theory = TheoryComponent(
                keys = listOf(data.key),
                scales = listOfNotNull(data.scale),
                chords = data.chords ?: emptyList(),
                melodyLine = null,
                intervals = emptyList(),
                timeSignature = "4/4"
            ),
            notation = listOf(
                NotationData(
                    type = NotationType.TAB,
                    content = "",
                    measureCount = (notes.size / 4) + 1,
                    encoding = null
                )
            ),
            playback = PlaybackSettings(
                bpm = data.bpm,
                loop = false,
                metronome = true,
                volume = 0.8f,
                repeatCount = 1
            ),
            notes = notes,
            createdTimestamp = System.currentTimeMillis(),
            modifiedTimestamp = System.currentTimeMillis(),
            creatorId = "ai",
            isAiGenerated = true,
            aiPrompt = originalPrompt
        )
    }
    
    private fun createFallbackExercise(prompt: String, context: AIPromptContext): Exercise {
        // Create a simple scale exercise as fallback
        val notes = listOf(
            Note(1, 0, 0, "E", 1),
            Note(2, 2, 1, "B", 1),
            Note(3, 2, 2, "D", 1),
            Note(4, 0, 3, "G", 1)
        )
        
        return Exercise(
            id = java.util.UUID.randomUUID().toString(),
            title = "AI Generated Exercise",
            description = "Exercise generated from: $prompt",
            instrument = context.instrument,
            tags = listOf("ai-generated", "fallback"),
            difficulty = context.difficulty,
            fretboard = FretboardConstraint(
                minFret = 0,
                maxFret = 12,
                allowedStrings = (1..context.instrument.getDefaultStringCount()).toList(),
                numStrings = context.instrument.getDefaultStringCount()
            ),
            theory = TheoryComponent(
                keys = listOf(context.key),
                scales = listOf("major"),
                chords = emptyList(),
                melodyLine = null,
                intervals = emptyList(),
                timeSignature = "4/4"
            ),
            notation = listOf(
                NotationData(
                    type = NotationType.TAB,
                    content = "",
                    measureCount = 1,
                    encoding = null
                )
            ),
            playback = PlaybackSettings(
                bpm = 120,
                loop = false,
                metronome = true,
                volume = 0.8f,
                repeatCount = 1
            ),
            notes = notes,
            createdTimestamp = System.currentTimeMillis(),
            modifiedTimestamp = System.currentTimeMillis(),
            creatorId = "ai",
            isAiGenerated = true,
            aiPrompt = prompt
        )
    }
    
    fun getSuggestedPrompts(instrument: InstrumentType, difficulty: DifficultyLevel): List<String> {
        return when (difficulty) {
            DifficultyLevel.BEGINNER -> listOf(
                "Create a simple C major scale exercise",
                "Generate basic chord progression practice",
                "Make an exercise for learning open chords",
                "Create a simple fingerpicking pattern",
                "Generate basic strumming patterns"
            )
            DifficultyLevel.INTERMEDIATE -> listOf(
                "Create a blues scale exercise in E",
                "Generate a barre chord progression",
                "Make an arpeggio exercise for major chords",
                "Create a rhythm exercise with syncopation",
                "Generate a pentatonic scale workout"
            )
            DifficultyLevel.ADVANCED -> listOf(
                "Create complex jazz chord progressions",
                "Generate advanced fingerpicking patterns",
                "Make a sweep picking exercise",
                "Create modal interchange exercises",
                "Generate polyrhythmic practice patterns"
            )
            DifficultyLevel.EXPERT -> listOf(
                "Create virtuosic technical exercises",
                "Generate complex polyrhythmic patterns",
                "Make advanced jazz fusion exercises",
                "Create atonal composition exercises",
                "Generate advanced improvisation frameworks"
            )
        }
    }
}