package com.quantfidential.guitarbasspractice.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quantfidential.guitarbasspractice.domain.model.Note
import com.quantfidential.guitarbasspractice.domain.model.NotationType
import com.quantfidential.guitarbasspractice.domain.model.InstrumentType

data class TabNote(
    val string: Int,
    val fret: Int,
    val beat: Float,
    val duration: Float = 1f,
    val isHighlighted: Boolean = false
)

@Composable
fun NotationRenderer(
    notationType: NotationType = NotationType.TAB,
    notes: List<TabNote> = emptyList(),
    instrument: InstrumentType = InstrumentType.GUITAR,
    timeSignature: String = "4/4",
    currentBeat: Float = -1f,
    modifier: Modifier = Modifier
) {
    var selectedNotationType by remember { mutableStateOf(notationType) }
    val scrollState = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with notation type selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                NotationTypeSelector(
                    selectedType = selectedNotationType,
                    onTypeSelected = { selectedNotationType = it }
                )
            }

            // Scrollable notation content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(scrollState)
            ) {
                when (selectedNotationType) {
                    NotationType.TAB -> TabNotation(
                        notes = notes,
                        instrument = instrument,
                        currentBeat = currentBeat,
                        modifier = Modifier.fillMaxSize()
                    )
                    NotationType.STAVE -> StaveNotation(
                        notes = notes,
                        timeSignature = timeSignature,
                        currentBeat = currentBeat,
                        modifier = Modifier.fillMaxSize()
                    )
                    NotationType.CHORD_CHART -> ChordChartNotation(
                        notes = notes,
                        modifier = Modifier.fillMaxSize()
                    )
                    NotationType.FRETBOARD -> FretboardNotation(
                        notes = notes,
                        instrument = instrument,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun NotationTypeSelector(
    selectedType: NotationType,
    onTypeSelected: (NotationType) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        NotationType.values().forEach { type ->
            FilterChip(
                onClick = { onTypeSelected(type) },
                label = { 
                    Text(
                        text = when (type) {
                            NotationType.TAB -> "TAB"
                            NotationType.STAVE -> "Staff"
                            NotationType.CHORD_CHART -> "Chords"
                            NotationType.FRETBOARD -> "Fretboard"
                        },
                        fontSize = 12.sp
                    ) 
                },
                selected = selectedType == type
            )
        }
    }
}

@Composable
private fun TabNotation(
    notes: List<TabNote>,
    instrument: InstrumentType,
    currentBeat: Float,
    modifier: Modifier = Modifier
) {
    val numStrings = instrument.getDefaultStringCount()
    val tuning = instrument.getStandardTuning()
    
    Canvas(modifier = modifier.width(800.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val stringSpacing = canvasHeight / (numStrings + 1)
        val beatWidth = 60f
        
        // Draw tab lines (one for each string)
        for (string in 1..numStrings) {
            val y = string * stringSpacing
            drawLine(
                color = Color.Gray,
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = 1.dp.toPx()
            )
            
            // Draw string names
            if (string - 1 < tuning.size) {
                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 12.sp.toPx()
                        textAlign = android.graphics.Paint.Align.RIGHT
                        isFakeBoldText = true
                    }
                    drawText(
                        tuning[string - 1],
                        -10f,
                        y + 4.dp.toPx(),
                        textPaint
                    )
                }
            }
        }
        
        // Draw time markers
        val numBeats = (canvasWidth / beatWidth).toInt()
        for (beat in 0..numBeats) {
            val x = beat * beatWidth
            drawLine(
                color = Color.LightGray,
                start = Offset(x, 0f),
                end = Offset(x, canvasHeight),
                strokeWidth = if (beat % 4 == 0) 2.dp.toPx() else 0.5.dp.toPx()
            )
        }
        
        // Draw current position indicator
        if (currentBeat >= 0) {
            val x = currentBeat * beatWidth
            drawLine(
                color = Color.Red,
                start = Offset(x, 0f),
                end = Offset(x, canvasHeight),
                strokeWidth = 3.dp.toPx()
            )
        }
        
        // Draw notes
        notes.forEach { note ->
            if (note.string > 0 && note.string <= numStrings) {
                val x = note.beat * beatWidth
                val y = note.string * stringSpacing
                val color = if (note.isHighlighted) Color.Red else Color.Black
                
                // Draw fret number
                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = android.graphics.Paint().apply {
                        this.color = if (note.isHighlighted) android.graphics.Color.RED else android.graphics.Color.BLACK
                        textSize = 14.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    drawText(
                        note.fret.toString(),
                        x,
                        y + 6.dp.toPx(),
                        textPaint
                    )
                }
            }
        }
    }
}

@Composable
private fun StaveNotation(
    notes: List<TabNote>,
    timeSignature: String,
    currentBeat: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.width(800.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val lineSpacing = canvasHeight / 8
        val staffTop = lineSpacing * 2
        val beatWidth = 80f
        
        // Draw staff lines
        for (line in 0..4) {
            val y = staffTop + line * lineSpacing
            drawLine(
                color = Color.Black,
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        
        // Draw treble clef (simplified)
        drawContext.canvas.nativeCanvas.apply {
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 48.sp.toPx()
                textAlign = android.graphics.Paint.Align.LEFT
            }
            drawText(
                "𝄞",
                20f,
                staffTop + 2 * lineSpacing + 16.dp.toPx(),
                textPaint
            )
        }
        
        // Draw time signature
        drawContext.canvas.nativeCanvas.apply {
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 20.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
            }
            drawText(
                timeSignature.split("/")[0],
                100f,
                staffTop + lineSpacing + 8.dp.toPx(),
                textPaint
            )
            drawText(
                timeSignature.split("/")[1],
                100f,
                staffTop + 3 * lineSpacing + 8.dp.toPx(),
                textPaint
            )
        }
        
        // Draw measure lines
        val numMeasures = (canvasWidth / (beatWidth * 4)).toInt()
        for (measure in 0..numMeasures) {
            val x = 140f + measure * beatWidth * 4
            drawLine(
                color = Color.Black,
                start = Offset(x, staffTop),
                end = Offset(x, staffTop + 4 * lineSpacing),
                strokeWidth = 2.dp.toPx()
            )
        }
        
        // Draw current position indicator
        if (currentBeat >= 0) {
            val x = 140f + currentBeat * beatWidth
            drawLine(
                color = Color.Red,
                start = Offset(x, staffTop - 10.dp.toPx()),
                end = Offset(x, staffTop + 4 * lineSpacing + 10.dp.toPx()),
                strokeWidth = 3.dp.toPx()
            )
        }
        
        // Draw note heads (simplified)
        notes.forEach { note ->
            val x = 140f + note.beat * beatWidth
            val noteColor = if (note.isHighlighted) Color.Red else Color.Black
            
            // Simple note head representation
            drawCircle(
                color = noteColor,
                radius = 6.dp.toPx(),
                center = Offset(x, staffTop + 2 * lineSpacing)
            )
        }
    }
}

@Composable
private fun ChordChartNotation(
    notes: List<TabNote>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.width(600.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        drawContext.canvas.nativeCanvas.apply {
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 16.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
            }
            drawText(
                "Chord Chart View",
                canvasWidth / 2,
                canvasHeight / 2,
                textPaint
            )
        }
    }
}

@Composable
private fun FretboardNotation(
    notes: List<TabNote>,
    instrument: InstrumentType,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.width(600.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        drawContext.canvas.nativeCanvas.apply {
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 16.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
            }
            drawText(
                "Fretboard Notation View",
                canvasWidth / 2,
                canvasHeight / 2,
                textPaint
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotationRendererPreview() {
    MaterialTheme {
        NotationRenderer(
            notationType = NotationType.TAB,
            notes = listOf(
                TabNote(1, 0, 0f, 1f),
                TabNote(2, 2, 1f, 1f),
                TabNote(3, 2, 2f, 1f),
                TabNote(4, 0, 3f, 1f, isHighlighted = true),
                TabNote(1, 3, 4f, 1f),
                TabNote(2, 3, 5f, 1f),
                TabNote(3, 2, 6f, 1f),
                TabNote(4, 0, 7f, 1f)
            ),
            currentBeat = 3.5f
        )
    }
}