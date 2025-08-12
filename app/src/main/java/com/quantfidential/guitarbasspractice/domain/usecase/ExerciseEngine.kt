package com.quantfidential.guitarbasspractice.domain.usecase

import com.quantfidential.guitarbasspractice.domain.model.Exercise
import com.quantfidential.guitarbasspractice.domain.model.Note
import com.quantfidential.guitarbasspractice.util.FretPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

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

@Singleton
class ExerciseEngine @Inject constructor() {
    
    private var currentExercise: Exercise? = null
    private var playbackState = ExercisePlaybackState()
    
    fun executeExercise(exercise: Exercise): Flow<ExercisePlaybackState> = flow {
        currentExercise = exercise
        val notes = exercise.notes.sortedBy { it.beat }
        val totalBeats = notes.maxOfOrNull { it.beat + it.duration }?.toFloat() ?: 4f
        val beatDurationMs = (60000 / playbackState.bpm).toLong()
        
        playbackState = playbackState.copy(
            bpm = exercise.playback.bpm,
            loop = exercise.playback.loop,
            metronome = exercise.playback.metronome
        )
        
        emit(playbackState)
        
        while (true) {
            if (!playbackState.isPlaying) {
                delay(100)
                continue
            }
            
            val currentBeat = playbackState.currentBeat
            val activeNotes = notes.filter { note ->
                note.beat.toFloat() <= currentBeat && note.beat.toFloat() + note.duration.toFloat() > currentBeat
            }
            
            val highlightedPositions = activeNotes.map { note ->
                FretPosition(
                    string = note.stringNumber,
                    fret = note.fret,
                    note = note.noteName,
                    isHighlighted = true
                )
            }
            
            val progress = if (totalBeats > 0) currentBeat / totalBeats else 0f
            val currentNoteIndex = notes.indexOfFirst { it.beat.toFloat() > currentBeat }.let { 
                if (it == -1) notes.size else it 
            }
            
            playbackState = playbackState.copy(
                currentBeat = currentBeat,
                currentNoteIndex = currentNoteIndex,
                progress = progress,
                highlightedPositions = highlightedPositions
            )
            
            emit(playbackState)
            
            // Check if we've reached the end
            if (currentBeat >= totalBeats) {
                if (playbackState.loop) {
                    playbackState = playbackState.copy(currentBeat = 0f)
                } else {
                    playbackState = playbackState.copy(
                        isPlaying = false,
                        currentBeat = 0f,
                        highlightedPositions = emptyList()
                    )
                }
            } else {
                // Advance beat
                val nextBeat = currentBeat + (beatDurationMs / 1000f)
                playbackState = playbackState.copy(currentBeat = nextBeat)
            }
            
            delay(beatDurationMs / 4) // Update 4 times per beat for smooth animation
        }
    }
    
    fun handleEvent(event: ExerciseEngineEvent): ExercisePlaybackState {
        playbackState = when (event) {
            is ExerciseEngineEvent.Play -> playbackState.copy(isPlaying = true)
            is ExerciseEngineEvent.Pause -> playbackState.copy(isPlaying = false)
            is ExerciseEngineEvent.Stop -> playbackState.copy(
                isPlaying = false,
                currentBeat = 0f,
                currentNoteIndex = 0,
                progress = 0f,
                highlightedPositions = emptyList()
            )
            is ExerciseEngineEvent.Reset -> playbackState.copy(
                isPlaying = false,
                currentBeat = 0f,
                currentNoteIndex = 0,
                progress = 0f,
                highlightedPositions = emptyList()
            )
            is ExerciseEngineEvent.SetBpm -> playbackState.copy(bpm = event.bpm)
            is ExerciseEngineEvent.SetLoop -> playbackState.copy(loop = event.loop)
            is ExerciseEngineEvent.SetMetronome -> playbackState.copy(metronome = event.metronome)
            is ExerciseEngineEvent.SeekTo -> playbackState.copy(currentBeat = event.beat)
        }
        return playbackState
    }
    
    fun getCurrentState(): ExercisePlaybackState = playbackState
}

@Singleton
class MetronomeEngine @Inject constructor() {
    
    fun startMetronome(bpm: Int): Flow<Boolean> = flow {
        val beatDurationMs = (60000 / bpm).toLong()
        while (true) {
            emit(true) // Beat tick
            delay(100) // Short tick sound duration
            emit(false)
            delay(beatDurationMs - 100)
        }
    }
}