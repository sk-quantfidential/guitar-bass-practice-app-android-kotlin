package com.quantfidential.guitarbasspractice.domain.usecase

import com.quantfidential.guitarbasspractice.domain.model.Exercise
import com.quantfidential.guitarbasspractice.domain.model.Note
import com.quantfidential.guitarbasspractice.util.FretPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.ensureActive
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

data class ExercisePlaybackState(
    val isPlaying: Boolean = false,
    val currentBeat: Float = 0f,
    val currentNoteIndex: Int = 0,
    val progress: Float = 0f,
    val highlightedPositions: List<FretPosition> = emptyList(),
    val bpm: Int = 120,
    val loop: Boolean = false,
    val metronome: Boolean = false
)

sealed class ExerciseEngineEvent {
    object Play : ExerciseEngineEvent()
    object Pause : ExerciseEngineEvent()
    object Stop : ExerciseEngineEvent()
    object Reset : ExerciseEngineEvent()
    data class SetBpm(val bpm: Int) : ExerciseEngineEvent()
    data class SetLoop(val loop: Boolean) : ExerciseEngineEvent()
    data class SetMetronome(val metronome: Boolean) : ExerciseEngineEvent()
    data class SeekTo(val beat: Float) : ExerciseEngineEvent()
}

/**
 * Stateless exercise playback engine that manages exercise execution.
 * State should be maintained by the calling component (e.g., ViewModel).
 */
class ExerciseEngine @Inject constructor() {
    
    /**
     * Executes an exercise with proper cancellation support and memory management.
     * @param exercise The exercise to execute
     * @param initialState The initial playback state
     * @return Flow of playback states during execution
     */
    fun executeExercise(
        exercise: Exercise, 
        initialState: ExercisePlaybackState = ExercisePlaybackState()
    ): Flow<ExercisePlaybackState> = flow {
        val notes = exercise.notes.sortedBy { it.beat }
        val totalBeats = notes.maxOfOrNull { it.beat + it.duration }?.toFloat() ?: 4f
        
        var currentState = initialState.copy(
            bpm = exercise.playback.bpm,
            loop = exercise.playback.loop,
            metronome = exercise.playback.metronome,
            isPlaying = true
        )
        
        emit(currentState)
        
        try {
            while (currentState.isPlaying) {
                coroutineContext.ensureActive() // Check for cancellation
                
                val beatDurationMs = calculateBeatDuration(currentState.bpm)
                val currentBeat = currentState.currentBeat
                
                // Calculate active notes and positions
                val activeNotes = findActiveNotes(notes, currentBeat)
                val highlightedPositions = createHighlightedPositions(activeNotes)
                val progress = calculateProgress(currentBeat, totalBeats)
                val currentNoteIndex = findCurrentNoteIndex(notes, currentBeat)
                
                currentState = currentState.copy(
                    currentBeat = currentBeat,
                    progress = progress,
                    currentNoteIndex = currentNoteIndex,
                    highlightedPositions = highlightedPositions
                )
                
                emit(currentState)
                
                // Advance to next beat
                val nextBeat = currentBeat + BEAT_INCREMENT
                
                if (nextBeat >= totalBeats) {
                    if (currentState.loop) {
                        currentState = currentState.copy(currentBeat = 0f)
                    } else {
                        currentState = currentState.copy(
                            isPlaying = false, 
                            currentBeat = 0f,
                            progress = 1f
                        )
                        emit(currentState)
                        break
                    }
                } else {
                    currentState = currentState.copy(currentBeat = nextBeat)
                }
                
                delay(beatDurationMs / 4)
            }
        } catch (e: Exception) {
            // Emit stopped state on any error
            emit(currentState.copy(isPlaying = false))
            throw e
        }
    }
    
    /**
     * Handles playback events and returns updated state
     */
    fun handleEvent(
        event: ExerciseEngineEvent, 
        currentState: ExercisePlaybackState
    ): ExercisePlaybackState {
        return when (event) {
            is ExerciseEngineEvent.Play -> currentState.copy(isPlaying = true)
            is ExerciseEngineEvent.Pause -> currentState.copy(isPlaying = false)
            is ExerciseEngineEvent.Stop -> currentState.copy(
                isPlaying = false, 
                currentBeat = 0f, 
                currentNoteIndex = 0, 
                progress = 0f,
                highlightedPositions = emptyList()
            )
            is ExerciseEngineEvent.Reset -> ExercisePlaybackState(bpm = currentState.bpm)
            is ExerciseEngineEvent.SetBpm -> currentState.copy(bpm = event.bpm.coerceIn(MIN_BPM, MAX_BPM))
            is ExerciseEngineEvent.SetLoop -> currentState.copy(loop = event.loop)
            is ExerciseEngineEvent.SetMetronome -> currentState.copy(metronome = event.metronome)
            is ExerciseEngineEvent.SeekTo -> currentState.copy(currentBeat = event.beat.coerceAtLeast(0f))
        }
    }
    
    private fun calculateBeatDuration(bpm: Int): Long {
        return (MILLISECONDS_PER_MINUTE / bpm.coerceIn(MIN_BPM, MAX_BPM)).toLong()
    }
    
    private fun findActiveNotes(notes: List<Note>, currentBeat: Float): List<Note> {
        return notes.filter { note ->
            val noteStart = note.beat.toFloat()
            val noteEnd = noteStart + note.duration.toFloat()
            currentBeat >= noteStart && currentBeat < noteEnd
        }
    }
    
    private fun createHighlightedPositions(activeNotes: List<Note>): List<FretPosition> {
        return activeNotes.map { note ->
            FretPosition(
                string = note.stringNumber.coerceIn(1, 6),
                fret = note.fret.coerceIn(0, 24),
                note = note.noteName,
                isHighlighted = true
            )
        }
    }
    
    private fun calculateProgress(currentBeat: Float, totalBeats: Float): Float {
        return if (totalBeats > 0) (currentBeat / totalBeats).coerceIn(0f, 1f) else 0f
    }
    
    private fun findCurrentNoteIndex(notes: List<Note>, currentBeat: Float): Int {
        return notes.indexOfFirst { it.beat.toFloat() > currentBeat }.let { 
            if (it == -1) notes.size else it 
        }.coerceIn(0, notes.size)
    }
    
    companion object {
        private const val BEAT_INCREMENT = 0.25f
        private const val MILLISECONDS_PER_MINUTE = 60000
        private const val MIN_BPM = 40
        private const val MAX_BPM = 300
    }
}