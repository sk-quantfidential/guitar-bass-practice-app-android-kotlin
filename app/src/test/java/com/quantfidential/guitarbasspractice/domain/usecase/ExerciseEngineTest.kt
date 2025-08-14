package com.quantfidential.guitarbasspractice.domain.usecase

import com.quantfidential.guitarbasspractice.domain.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExerciseEngineTest {
    
    private lateinit var exerciseEngine: ExerciseEngine
    
    @Before
    fun setup() {
        exerciseEngine = ExerciseEngine()
    }
    
    @Test
    fun testInitialPlaybackState() {
        val initialState = ExercisePlaybackState()
        
        assertFalse(initialState.isPlaying)
        assertEquals(0f, initialState.currentBeat, 0.01f)
        assertEquals(0, initialState.currentNoteIndex)
        assertEquals(0f, initialState.progress, 0.01f)
        assertTrue(initialState.highlightedPositions.isEmpty())
        assertEquals(120, initialState.bpm)
        assertFalse(initialState.loop)
        assertFalse(initialState.metronome)
    }
    
    @Test
    fun testHandleEvent_Play() {
        val initialState = ExercisePlaybackState()
        val newState = exerciseEngine.handleEvent(ExerciseEngineEvent.Play, initialState)
        
        assertTrue(newState.isPlaying)
    }
    
    @Test
    fun testHandleEvent_Pause() {
        val initialState = ExercisePlaybackState(isPlaying = true)
        val pausedState = exerciseEngine.handleEvent(ExerciseEngineEvent.Pause, initialState)
        
        assertFalse(pausedState.isPlaying)
    }
    
    @Test
    fun testHandleEvent_Stop() {
        val initialState = ExercisePlaybackState(isPlaying = true, currentBeat = 2.5f)
        val stoppedState = exerciseEngine.handleEvent(ExerciseEngineEvent.Stop, initialState)
        
        assertFalse(stoppedState.isPlaying)
        assertEquals(0f, stoppedState.currentBeat, 0.01f)
        assertEquals(0, stoppedState.currentNoteIndex)
        assertEquals(0f, stoppedState.progress, 0.01f)
        assertTrue(stoppedState.highlightedPositions.isEmpty())
    }
    
    @Test
    fun testHandleEvent_Reset() {
        val initialState = ExercisePlaybackState(isPlaying = true, currentBeat = 3.0f)
        val resetState = exerciseEngine.handleEvent(ExerciseEngineEvent.Reset, initialState)
        
        assertFalse(resetState.isPlaying)
        assertEquals(0f, resetState.currentBeat, 0.01f)
        assertEquals(0, resetState.currentNoteIndex)
        assertEquals(0f, resetState.progress, 0.01f)
        assertTrue(resetState.highlightedPositions.isEmpty())
    }
    
    @Test
    fun testHandleEvent_SetBpm() {
        val initialState = ExercisePlaybackState()
        val newState = exerciseEngine.handleEvent(ExerciseEngineEvent.SetBpm(140), initialState)
        
        assertEquals(140, newState.bpm)
    }
    
    @Test
    fun testHandleEvent_SetLoop() {
        val initialState = ExercisePlaybackState()
        val newState = exerciseEngine.handleEvent(ExerciseEngineEvent.SetLoop(true), initialState)
        
        assertTrue(newState.loop)
    }
    
    @Test
    fun testHandleEvent_SetMetronome() {
        val initialState = ExercisePlaybackState()
        val newState = exerciseEngine.handleEvent(ExerciseEngineEvent.SetMetronome(true), initialState)
        
        assertTrue(newState.metronome)
    }
    
    @Test
    fun testHandleEvent_SeekTo() {
        val initialState = ExercisePlaybackState()
        val seekPosition = 2.5f
        val newState = exerciseEngine.handleEvent(ExerciseEngineEvent.SeekTo(seekPosition), initialState)
        
        assertEquals(seekPosition, newState.currentBeat, 0.01f)
    }
    
    @Test
    fun testExecuteExercise_initialState() = runBlocking {
        val exercise = createTestExercise()
        
        val firstState = exerciseEngine.executeExercise(exercise).first()
        
        assertEquals(exercise.playback.bpm, firstState.bpm)
        assertEquals(exercise.playback.loop, firstState.loop)
        assertEquals(exercise.playback.metronome, firstState.metronome)
        assertTrue(firstState.isPlaying)
    }
    
    @Test
    fun testExercisePlaybackState_dataClass() {
        val state = ExercisePlaybackState(
            isPlaying = true,
            currentBeat = 1.5f,
            currentNoteIndex = 2,
            progress = 0.375f,
            highlightedPositions = listOf(
                com.quantfidential.guitarbasspractice.util.FretPosition(1, 3, "G", true)
            ),
            bpm = 100,
            loop = true,
            metronome = false
        )
        
        assertTrue(state.isPlaying)
        assertEquals(1.5f, state.currentBeat, 0.01f)
        assertEquals(2, state.currentNoteIndex)
        assertEquals(0.375f, state.progress, 0.01f)
        assertEquals(1, state.highlightedPositions.size)
        assertEquals(100, state.bpm)
        assertTrue(state.loop)
        assertFalse(state.metronome)
    }
    
    @Test
    fun testExerciseEngineEvent_sealedClass() {
        val playEvent = ExerciseEngineEvent.Play
        val pauseEvent = ExerciseEngineEvent.Pause
        val stopEvent = ExerciseEngineEvent.Stop
        val resetEvent = ExerciseEngineEvent.Reset
        val setBpmEvent = ExerciseEngineEvent.SetBpm(130)
        val setLoopEvent = ExerciseEngineEvent.SetLoop(true)
        val setMetronomeEvent = ExerciseEngineEvent.SetMetronome(false)
        val seekEvent = ExerciseEngineEvent.SeekTo(4.2f)
        
        assertTrue(playEvent is ExerciseEngineEvent.Play)
        assertTrue(pauseEvent is ExerciseEngineEvent.Pause)
        assertTrue(stopEvent is ExerciseEngineEvent.Stop)
        assertTrue(resetEvent is ExerciseEngineEvent.Reset)
        assertTrue(setBpmEvent is ExerciseEngineEvent.SetBpm)
        assertTrue(setLoopEvent is ExerciseEngineEvent.SetLoop)
        assertTrue(setMetronomeEvent is ExerciseEngineEvent.SetMetronome)
        assertTrue(seekEvent is ExerciseEngineEvent.SeekTo)
        
        assertEquals(130, (setBpmEvent as ExerciseEngineEvent.SetBpm).bpm)
        assertTrue((setLoopEvent as ExerciseEngineEvent.SetLoop).loop)
        assertFalse((setMetronomeEvent as ExerciseEngineEvent.SetMetronome).metronome)
        assertEquals(4.2f, (seekEvent as ExerciseEngineEvent.SeekTo).beat, 0.01f)
    }
    
    @Test
    fun testBPMBounds() {
        val initialState = ExercisePlaybackState()
        
        // Test minimum BPM
        val minState = exerciseEngine.handleEvent(ExerciseEngineEvent.SetBpm(10), initialState)
        assertTrue("BPM should be clamped to minimum", minState.bpm >= 40)
        
        // Test maximum BPM  
        val maxState = exerciseEngine.handleEvent(ExerciseEngineEvent.SetBpm(500), initialState)
        assertTrue("BPM should be clamped to maximum", maxState.bpm <= 300)
    }
    
    @Test
    fun testChainedEvents() {
        // Test multiple events in sequence
        var state = ExercisePlaybackState()
        state = exerciseEngine.handleEvent(ExerciseEngineEvent.SetBpm(90), state)
        state = exerciseEngine.handleEvent(ExerciseEngineEvent.SetLoop(true), state)
        state = exerciseEngine.handleEvent(ExerciseEngineEvent.SetMetronome(true), state)
        state = exerciseEngine.handleEvent(ExerciseEngineEvent.Play, state)
        state = exerciseEngine.handleEvent(ExerciseEngineEvent.SeekTo(1.5f), state)
        
        assertEquals(90, state.bpm)
        assertTrue(state.loop)
        assertTrue(state.metronome)
        assertTrue(state.isPlaying)
        assertEquals(1.5f, state.currentBeat, 0.01f)
    }
    
    @Test
    fun testStateImmutability() {
        val initialState = ExercisePlaybackState()
        val modifiedState = exerciseEngine.handleEvent(ExerciseEngineEvent.Play, initialState)
        
        // Original state should not be affected
        assertFalse(initialState.isPlaying)
        assertTrue(modifiedState.isPlaying)
        
        // States should be different objects
        assertNotSame(initialState, modifiedState)
    }
    
    private fun createTestExercise(): Exercise {
        return Exercise(
            id = "test-exercise",
            title = "Test Exercise",
            description = "A test exercise",
            instrument = InstrumentType.GUITAR,
            tags = listOf("test"),
            difficulty = DifficultyLevel.BEGINNER,
            fretboard = FretboardConstraint(
                minFret = 0,
                maxFret = 12,
                allowedStrings = listOf(1, 2, 3, 4, 5, 6),
                numStrings = 6
            ),
            theory = TheoryComponent(
                keys = listOf("C"),
                scales = listOf("major"),
                chords = emptyList(),
                melodyLine = null,
                intervals = emptyList(),
                timeSignature = "4/4"
            ),
            notation = listOf(
                NotationData(
                    type = NotationType.TAB,
                    content = "test-content",
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
            notes = listOf(
                Note(1, 0, 0, "E", 1),
                Note(2, 0, 1, "A", 1),
                Note(3, 0, 2, "D", 1),
                Note(4, 0, 3, "G", 1)
            ),
            createdTimestamp = System.currentTimeMillis(),
            modifiedTimestamp = System.currentTimeMillis(),
            creatorId = "test-user",
            isAiGenerated = false,
            aiPrompt = null
        )
    }
}