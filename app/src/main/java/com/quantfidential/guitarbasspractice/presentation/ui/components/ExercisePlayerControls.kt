package com.quantfidential.guitarbasspractice.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quantfidential.guitarbasspractice.domain.usecase.ExerciseEngineEvent
import com.quantfidential.guitarbasspractice.domain.usecase.ExercisePlaybackState

@Composable
fun ExercisePlayerControls(
    playbackState: ExercisePlaybackState,
    onEvent: (ExerciseEngineEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress bar
            LinearProgressIndicator(
                progress = playbackState.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(bottom = 16.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Current position and BPM info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Beat: ${String.format("%.1f", playbackState.currentBeat)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "${playbackState.bpm} BPM",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Main control buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stop button
                IconButton(
                    onClick = { onEvent(ExerciseEngineEvent.Stop) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Play/Pause button
                FilledIconButton(
                    onClick = { 
                        if (playbackState.isPlaying) {
                            onEvent(ExerciseEngineEvent.Pause)
                        } else {
                            onEvent(ExerciseEngineEvent.Play)
                        }
                    },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Reset button
                IconButton(
                    onClick = { onEvent(ExerciseEngineEvent.Reset) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Replay,
                        contentDescription = "Reset",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Additional controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BPM controls
                BpmControl(
                    currentBpm = playbackState.bpm,
                    onBpmChange = { newBpm -> onEvent(ExerciseEngineEvent.SetBpm(newBpm)) }
                )
                
                // Loop toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Loop,
                        contentDescription = "Loop",
                        tint = if (playbackState.loop) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = playbackState.loop,
                        onCheckedChange = { onEvent(ExerciseEngineEvent.SetLoop(it)) }
                    )
                }
                
                // Metronome toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = "Metronome",
                        tint = if (playbackState.metronome) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = playbackState.metronome,
                        onCheckedChange = { onEvent(ExerciseEngineEvent.SetMetronome(it)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BpmControl(
    currentBpm: Int,
    onBpmChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { 
                if (currentBpm > 60) onBpmChange(currentBpm - 5) 
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Remove,
                contentDescription = "Decrease BPM",
                modifier = Modifier.size(16.dp)
            )
        }
        
        Text(
            text = "${currentBpm}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        IconButton(
            onClick = { 
                if (currentBpm < 200) onBpmChange(currentBpm + 5) 
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Increase BPM",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExercisePlayerControlsPreview() {
    MaterialTheme {
        ExercisePlayerControls(
            playbackState = ExercisePlaybackState(
                isPlaying = false,
                currentBeat = 2.5f,
                progress = 0.3f,
                bpm = 120,
                loop = true,
                metronome = false
            ),
            onEvent = {}
        )
    }
}