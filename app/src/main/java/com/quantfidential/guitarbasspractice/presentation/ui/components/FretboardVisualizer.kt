package com.quantfidential.guitarbasspractice.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quantfidential.guitarbasspractice.domain.model.InstrumentType
import com.quantfidential.guitarbasspractice.domain.model.Note

data class FretPosition(
    val string: Int,
    val fret: Int,
    val note: String,
    val isHighlighted: Boolean = false,
    val highlightColor: Color = Color.Blue
)

@Composable
fun FretboardVisualizer(
    instrument: InstrumentType = InstrumentType.GUITAR,
    minFret: Int = 0,
    maxFret: Int = 12,
    highlightedPositions: List<FretPosition> = emptyList(),
    onPositionClick: (string: Int, fret: Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val numStrings = instrument.getDefaultStringCount()
    val tuning = instrument.getStandardTuning()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with instrument name and fret range
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${instrument.name} Fretboard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Frets $minFret-$maxFret",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Fretboard Canvas
            FretboardCanvas(
                numStrings = numStrings,
                tuning = tuning,
                minFret = minFret,
                maxFret = maxFret,
                highlightedPositions = highlightedPositions,
                onPositionClick = onPositionClick,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun FretboardCanvas(
    numStrings: Int,
    tuning: List<String>,
    minFret: Int,
    maxFret: Int,
    highlightedPositions: List<FretPosition>,
    onPositionClick: (string: Int, fret: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    Canvas(
        modifier = modifier
            .clickable { }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        val fretSpacing = canvasWidth / (maxFret - minFret + 1)
        val stringSpacing = canvasHeight / (numStrings + 1)
        
        // Draw frets (vertical lines)
        for (fret in minFret..maxFret) {
            val x = fret * fretSpacing
            drawLine(
                color = Color.Gray,
                start = Offset(x, 0f),
                end = Offset(x, canvasHeight),
                strokeWidth = if (fret == 0) 6.dp.toPx() else 2.dp.toPx()
            )
        }
        
        // Draw strings (horizontal lines)
        for (string in 1..numStrings) {
            val y = string * stringSpacing
            drawLine(
                color = Color.Gray,
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = (numStrings - string + 1).dp.toPx()
            )
        }
        
        // Draw fret markers
        drawFretMarkers(
            minFret = minFret,
            maxFret = maxFret,
            fretSpacing = fretSpacing,
            canvasHeight = canvasHeight
        )
        
        // Draw highlighted positions
        highlightedPositions.forEach { position ->
            if (position.fret >= minFret && position.fret <= maxFret && 
                position.string > 0 && position.string <= numStrings) {
                val x = (position.fret - minFret + 0.5f) * fretSpacing
                val y = position.string * stringSpacing
                
                // Draw highlight circle
                drawCircle(
                    color = position.highlightColor,
                    radius = 12.dp.toPx(),
                    center = Offset(x, y)
                )
                
                // Draw note name
                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 12.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    drawText(
                        position.note,
                        x,
                        y + 4.dp.toPx(),
                        textPaint
                    )
                }
            }
        }
        
        // Draw string names (tuning)
        for (string in 1..numStrings) {
            val y = string * stringSpacing
            if (string - 1 < tuning.size) {
                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 14.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    drawText(
                        tuning[string - 1],
                        -20.dp.toPx(),
                        y + 4.dp.toPx(),
                        textPaint
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawFretMarkers(
    minFret: Int,
    maxFret: Int,
    fretSpacing: Float,
    canvasHeight: Float
) {
    val markerFrets = listOf(3, 5, 7, 9, 12, 15, 17, 19, 21, 24)
    val doubleMarkerFrets = listOf(12, 24)
    
    markerFrets.forEach { fret ->
        if (fret > minFret && fret <= maxFret) {
            val x = (fret - minFret - 0.5f) * fretSpacing
            
            if (doubleMarkerFrets.contains(fret)) {
                // Double dots for 12th and 24th frets
                drawCircle(
                    color = Color.LightGray,
                    radius = 4.dp.toPx(),
                    center = Offset(x, canvasHeight * 0.3f)
                )
                drawCircle(
                    color = Color.LightGray,
                    radius = 4.dp.toPx(),
                    center = Offset(x, canvasHeight * 0.7f)
                )
            } else {
                // Single dot
                drawCircle(
                    color = Color.LightGray,
                    radius = 4.dp.toPx(),
                    center = Offset(x, canvasHeight * 0.5f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FretboardVisualizerPreview() {
    MaterialTheme {
        FretboardVisualizer(
            instrument = InstrumentType.GUITAR,
            highlightedPositions = listOf(
                FretPosition(1, 3, "G", true, Color.Red),
                FretPosition(2, 2, "B", true, Color.Blue),
                FretPosition(3, 2, "D", true, Color.Green),
                FretPosition(4, 0, "G", true, Color.Yellow),
                FretPosition(5, 1, "B", true, Color.Magenta),
                FretPosition(6, 3, "G", true, Color.Cyan)
            )
        )
    }
}