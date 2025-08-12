package com.quantfidential.guitarbasspractice.util

object MusicTheoryUtil {
    private val chromaticScale = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    
    fun getNoteFromFret(openStringNote: String, fret: Int): String {
        val openNoteIndex = chromaticScale.indexOf(openStringNote)
        if (openNoteIndex == -1) return "?"
        
        val noteIndex = (openNoteIndex + fret) % chromaticScale.size
        return chromaticScale[noteIndex]
    }
    
    fun getScaleNotes(rootNote: String, scaleType: ScaleType): List<String> {
        val rootIndex = chromaticScale.indexOf(rootNote)
        if (rootIndex == -1) return emptyList()
        
        return scaleType.intervals.map { interval ->
            chromaticScale[(rootIndex + interval) % chromaticScale.size]
        }
    }
    
    fun getChordNotes(rootNote: String, chordType: ChordType): List<String> {
        val rootIndex = chromaticScale.indexOf(rootNote)
        if (rootIndex == -1) return emptyList()
        
        return chordType.intervals.map { interval ->
            chromaticScale[(rootIndex + interval) % chromaticScale.size]
        }
    }
    
    fun getAllNotesOnFretboard(tuning: List<String>, maxFret: Int): List<List<String>> {
        return tuning.map { openString ->
            (0..maxFret).map { fret ->
                getNoteFromFret(openString, fret)
            }
        }
    }
    
    fun findNotesOnFretboard(
        tuning: List<String>,
        targetNotes: List<String>,
        minFret: Int = 0,
        maxFret: Int = 12
    ): List<FretPosition> {
        val positions = mutableListOf<FretPosition>()
        
        tuning.forEachIndexed { stringIndex, openNote ->
            for (fret in minFret..maxFret) {
                val note = getNoteFromFret(openNote, fret)
                if (targetNotes.contains(note)) {
                    positions.add(
                        FretPosition(
                            string = stringIndex + 1,
                            fret = fret,
                            note = note,
                            isHighlighted = true
                        )
                    )
                }
            }
        }
        
        return positions
    }
}

enum class ScaleType(val intervals: List<Int>) {
    MAJOR(listOf(0, 2, 4, 5, 7, 9, 11)),
    MINOR(listOf(0, 2, 3, 5, 7, 8, 10)),
    PENTATONIC_MAJOR(listOf(0, 2, 4, 7, 9)),
    PENTATONIC_MINOR(listOf(0, 3, 5, 7, 10)),
    BLUES(listOf(0, 3, 5, 6, 7, 10)),
    DORIAN(listOf(0, 2, 3, 5, 7, 9, 10)),
    MIXOLYDIAN(listOf(0, 2, 4, 5, 7, 9, 10))
}

enum class ChordType(val intervals: List<Int>) {
    MAJOR(listOf(0, 4, 7)),
    MINOR(listOf(0, 3, 7)),
    MAJOR7(listOf(0, 4, 7, 11)),
    MINOR7(listOf(0, 3, 7, 10)),
    DOMINANT7(listOf(0, 4, 7, 10)),
    DIMINISHED(listOf(0, 3, 6)),
    AUGMENTED(listOf(0, 4, 8))
}