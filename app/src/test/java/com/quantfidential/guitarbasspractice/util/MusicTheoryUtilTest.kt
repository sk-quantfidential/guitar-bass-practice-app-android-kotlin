package com.quantfidential.guitarbasspractice.util

import org.junit.Assert.*
import org.junit.Test

class MusicTheoryUtilTest {

    @Test
    fun testGetNoteFromFret_openString() {
        assertEquals("E", MusicTheoryUtil.getNoteFromFret("E", 0))
        assertEquals("A", MusicTheoryUtil.getNoteFromFret("A", 0))
        assertEquals("D", MusicTheoryUtil.getNoteFromFret("D", 0))
    }

    @Test
    fun testGetNoteFromFret_frettedNotes() {
        assertEquals("F", MusicTheoryUtil.getNoteFromFret("E", 1))
        assertEquals("F#", MusicTheoryUtil.getNoteFromFret("E", 2))
        assertEquals("G", MusicTheoryUtil.getNoteFromFret("E", 3))
        assertEquals("C", MusicTheoryUtil.getNoteFromFret("A", 3))
        assertEquals("E", MusicTheoryUtil.getNoteFromFret("A", 7))
    }

    @Test
    fun testGetNoteFromFret_octaveWrapping() {
        assertEquals("E", MusicTheoryUtil.getNoteFromFret("E", 12))
        assertEquals("A", MusicTheoryUtil.getNoteFromFret("A", 12))
        assertEquals("F", MusicTheoryUtil.getNoteFromFret("E", 13))
    }

    @Test
    fun testGetNoteFromFret_invalidNote() {
        assertEquals("?", MusicTheoryUtil.getNoteFromFret("X", 0))
        assertEquals("?", MusicTheoryUtil.getNoteFromFret("", 0))
        assertEquals("?", MusicTheoryUtil.getNoteFromFret("H", 0))
    }

    @Test
    fun testGetScaleNotes_cMajor() {
        val cMajor = MusicTheoryUtil.getScaleNotes("C", ScaleType.MAJOR)
        assertEquals(listOf("C", "D", "E", "F", "G", "A", "B"), cMajor)
    }

    @Test
    fun testGetScaleNotes_cMinor() {
        val cMinor = MusicTheoryUtil.getScaleNotes("C", ScaleType.MINOR)
        assertEquals(listOf("C", "D", "D#", "F", "G", "G#", "A#"), cMinor)
    }

    @Test
    fun testGetScaleNotes_gMajor() {
        val gMajor = MusicTheoryUtil.getScaleNotes("G", ScaleType.MAJOR)
        assertEquals(listOf("G", "A", "B", "C", "D", "E", "F#"), gMajor)
    }

    @Test
    fun testGetScaleNotes_pentatonic() {
        val cPentatonicMajor = MusicTheoryUtil.getScaleNotes("C", ScaleType.PENTATONIC_MAJOR)
        assertEquals(listOf("C", "D", "E", "G", "A"), cPentatonicMajor)
        
        val cPentatonicMinor = MusicTheoryUtil.getScaleNotes("C", ScaleType.PENTATONIC_MINOR)
        assertEquals(listOf("C", "D#", "F", "G", "A#"), cPentatonicMinor)
    }

    @Test
    fun testGetScaleNotes_blues() {
        val cBlues = MusicTheoryUtil.getScaleNotes("C", ScaleType.BLUES)
        assertEquals(listOf("C", "D#", "F", "F#", "G", "A#"), cBlues)
    }

    @Test
    fun testGetScaleNotes_modes() {
        val cDorian = MusicTheoryUtil.getScaleNotes("C", ScaleType.DORIAN)
        assertEquals(listOf("C", "D", "D#", "F", "G", "A", "A#"), cDorian)
        
        val cMixolydian = MusicTheoryUtil.getScaleNotes("C", ScaleType.MIXOLYDIAN)
        assertEquals(listOf("C", "D", "E", "F", "G", "A", "A#"), cMixolydian)
    }

    @Test
    fun testGetScaleNotes_invalidRoot() {
        val invalidScale = MusicTheoryUtil.getScaleNotes("X", ScaleType.MAJOR)
        assertTrue(invalidScale.isEmpty())
    }

    @Test
    fun testGetChordNotes_majorChords() {
        val cMajor = MusicTheoryUtil.getChordNotes("C", ChordType.MAJOR)
        assertEquals(listOf("C", "E", "G"), cMajor)
        
        val gMajor = MusicTheoryUtil.getChordNotes("G", ChordType.MAJOR)
        assertEquals(listOf("G", "B", "D"), gMajor)
    }

    @Test
    fun testGetChordNotes_minorChords() {
        val cMinor = MusicTheoryUtil.getChordNotes("C", ChordType.MINOR)
        assertEquals(listOf("C", "D#", "G"), cMinor)
        
        val aMinor = MusicTheoryUtil.getChordNotes("A", ChordType.MINOR)
        assertEquals(listOf("A", "C", "E"), aMinor)
    }

    @Test
    fun testGetChordNotes_seventhChords() {
        val cMajor7 = MusicTheoryUtil.getChordNotes("C", ChordType.MAJOR7)
        assertEquals(listOf("C", "E", "G", "B"), cMajor7)
        
        val cMinor7 = MusicTheoryUtil.getChordNotes("C", ChordType.MINOR7)
        assertEquals(listOf("C", "D#", "G", "A#"), cMinor7)
        
        val cDom7 = MusicTheoryUtil.getChordNotes("C", ChordType.DOMINANT7)
        assertEquals(listOf("C", "E", "G", "A#"), cDom7)
    }

    @Test
    fun testGetChordNotes_alteratedChords() {
        val cDim = MusicTheoryUtil.getChordNotes("C", ChordType.DIMINISHED)
        assertEquals(listOf("C", "D#", "F#"), cDim)
        
        val cAug = MusicTheoryUtil.getChordNotes("C", ChordType.AUGMENTED)
        assertEquals(listOf("C", "E", "G#"), cAug)
    }

    @Test
    fun testGetChordNotes_invalidRoot() {
        val invalidChord = MusicTheoryUtil.getChordNotes("X", ChordType.MAJOR)
        assertTrue(invalidChord.isEmpty())
    }

    @Test
    fun testGetAllNotesOnFretboard_standardGuitar() {
        val guitarTuning = listOf("E", "A", "D", "G", "B", "E")
        val fretboardNotes = MusicTheoryUtil.getAllNotesOnFretboard(guitarTuning, 3)
        
        assertEquals(6, fretboardNotes.size) // 6 strings
        assertEquals(4, fretboardNotes[0].size) // 4 frets (0-3)
        
        // Test low E string
        assertEquals("E", fretboardNotes[0][0])
        assertEquals("F", fretboardNotes[0][1])
        assertEquals("F#", fretboardNotes[0][2])
        assertEquals("G", fretboardNotes[0][3])
        
        // Test A string
        assertEquals("A", fretboardNotes[1][0])
        assertEquals("A#", fretboardNotes[1][1])
        assertEquals("B", fretboardNotes[1][2])
        assertEquals("C", fretboardNotes[1][3])
    }

    @Test
    fun testFindNotesOnFretboard_specificNotes() {
        val guitarTuning = listOf("E", "A", "D", "G", "B", "E")
        val targetNotes = listOf("C", "E", "G") // C major chord
        
        val positions = MusicTheoryUtil.findNotesOnFretboard(guitarTuning, targetNotes, 0, 5)
        
        // Should find multiple positions for each note
        assertTrue(positions.isNotEmpty())
        assertTrue(positions.all { it.note in targetNotes })
        assertTrue(positions.all { it.fret in 0..5 })
        assertTrue(positions.all { it.string in 1..6 })
        assertTrue(positions.all { it.isHighlighted })
    }

    @Test
    fun testFindNotesOnFretboard_constrainedRange() {
        val guitarTuning = listOf("E", "A", "D", "G", "B", "E")
        val targetNotes = listOf("G")
        
        val positions = MusicTheoryUtil.findNotesOnFretboard(guitarTuning, targetNotes, 3, 3)
        
        // Should only find G notes on fret 3
        assertTrue(positions.all { it.fret == 3 })
        assertTrue(positions.all { it.note == "G" })
        
        // Check specific known positions for G on fret 3
        // String 1 (E): E + 3 semitones = G ✓
        // String 6 (E): E + 3 semitones = G ✓
        assertTrue(positions.any { it.string == 1 }) // Low E string, 3rd fret = G
        assertTrue(positions.any { it.string == 6 }) // High E string, 3rd fret = G
    }

    @Test
    fun testFindNotesOnFretboard_emptyTargetNotes() {
        val guitarTuning = listOf("E", "A", "D", "G", "B", "E")
        val positions = MusicTheoryUtil.findNotesOnFretboard(guitarTuning, emptyList(), 0, 12)
        
        assertTrue(positions.isEmpty())
    }

    @Test
    fun testFindNotesOnFretboard_noMatchingNotes() {
        val guitarTuning = listOf("E", "A", "D", "G", "B", "E")
        val targetNotes = listOf("X", "Y", "Z") // Non-existent notes
        
        val positions = MusicTheoryUtil.findNotesOnFretboard(guitarTuning, targetNotes, 0, 12)
        
        assertTrue(positions.isEmpty())
    }

    @Test
    fun testScaleTypeIntervals() {
        assertEquals(listOf(0, 2, 4, 5, 7, 9, 11), ScaleType.MAJOR.intervals)
        assertEquals(listOf(0, 2, 3, 5, 7, 8, 10), ScaleType.MINOR.intervals)
        assertEquals(listOf(0, 2, 4, 7, 9), ScaleType.PENTATONIC_MAJOR.intervals)
        assertEquals(listOf(0, 3, 5, 7, 10), ScaleType.PENTATONIC_MINOR.intervals)
        assertEquals(listOf(0, 3, 5, 6, 7, 10), ScaleType.BLUES.intervals)
        assertEquals(listOf(0, 2, 3, 5, 7, 9, 10), ScaleType.DORIAN.intervals)
        assertEquals(listOf(0, 2, 4, 5, 7, 9, 10), ScaleType.MIXOLYDIAN.intervals)
    }

    @Test
    fun testChordTypeIntervals() {
        assertEquals(listOf(0, 4, 7), ChordType.MAJOR.intervals)
        assertEquals(listOf(0, 3, 7), ChordType.MINOR.intervals)
        assertEquals(listOf(0, 4, 7, 11), ChordType.MAJOR7.intervals)
        assertEquals(listOf(0, 3, 7, 10), ChordType.MINOR7.intervals)
        assertEquals(listOf(0, 4, 7, 10), ChordType.DOMINANT7.intervals)
        assertEquals(listOf(0, 3, 6), ChordType.DIMINISHED.intervals)
        assertEquals(listOf(0, 4, 8), ChordType.AUGMENTED.intervals)
    }

    @Test
    fun testFretPosition_dataClass() {
        val position = FretPosition(
            string = 1,
            fret = 3,
            note = "G",
            isHighlighted = true
        )
        
        assertEquals(1, position.string)
        assertEquals(3, position.fret)
        assertEquals("G", position.note)
        assertTrue(position.isHighlighted)
        
        // Test default value
        val defaultPosition = FretPosition(string = 2, fret = 5, note = "A")
        assertFalse(defaultPosition.isHighlighted)
    }
}