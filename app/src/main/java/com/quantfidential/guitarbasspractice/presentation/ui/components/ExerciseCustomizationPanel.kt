package com.quantfidential.guitarbasspractice.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.quantfidential.guitarbasspractice.domain.model.InstrumentType
import com.quantfidential.guitarbasspractice.domain.model.DifficultyLevel
import com.quantfidential.guitarbasspractice.domain.usecase.CustomizationOptions
import com.quantfidential.guitarbasspractice.domain.usecase.ExerciseTemplate
import com.quantfidential.guitarbasspractice.domain.usecase.ExerciseTemplates

@Composable
fun ExerciseCustomizationPanel(
    options: CustomizationOptions,
    onOptionsChanged: (CustomizationOptions) -> Unit,
    onCreateExercise: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Customize Exercise",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Exercise Templates
            item {
                ExerciseTemplateSelector(
                    onTemplateSelected = { template ->
                        val newOptions = when (template.type) {
                            com.quantfidential.guitarbasspractice.domain.usecase.ExerciseType.SCALE_PRACTICE -> 
                                options.copy(scales = listOf("major"), chords = emptyList())
                            com.quantfidential.guitarbasspractice.domain.usecase.ExerciseType.CHORD_PROGRESSION -> 
                                options.copy(chords = listOf("C", "Am", "F", "G"), scales = emptyList())
                            com.quantfidential.guitarbasspractice.domain.usecase.ExerciseType.ARPEGGIO_PRACTICE -> 
                                options.copy(scales = listOf("arpeggio"), chords = emptyList())
                            else -> options
                        }
                        onOptionsChanged(newOptions)
                    }
                )
            }
            
            // Basic Settings
            item {
                BasicSettingsSection(
                    options = options,
                    onOptionsChanged = onOptionsChanged
                )
            }
            
            // Fretboard Constraints
            item {
                FretboardConstraintsSection(
                    options = options,
                    onOptionsChanged = onOptionsChanged
                )
            }
            
            // Music Theory Settings
            item {
                MusicTheorySection(
                    options = options,
                    onOptionsChanged = onOptionsChanged
                )
            }
            
            // Playback Settings
            item {
                PlaybackSettingsSection(
                    options = options,
                    onOptionsChanged = onOptionsChanged
                )
            }
            
            // Create Button
            item {
                FilledTonalButton(
                    onClick = onCreateExercise,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Exercise")
                }
            }
        }
    }
}

@Composable
private fun ExerciseTemplateSelector(
    onTemplateSelected: (ExerciseTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Templates",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(ExerciseTemplates.getAllTemplates()) { template ->
                FilterChip(
                    onClick = { onTemplateSelected(template) },
                    label = { Text(template.title) },
                    selected = false
                )
            }
        }
    }
}

@Composable
private fun BasicSettingsSection(
    options: CustomizationOptions,
    onOptionsChanged: (CustomizationOptions) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Basic Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Instrument Selector
            var expandedInstrument by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedInstrument,
                onExpandedChange = { expandedInstrument = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = options.instrument.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Instrument") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedInstrument) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedInstrument,
                    onDismissRequest = { expandedInstrument = false }
                ) {
                    InstrumentType.values().forEach { instrument ->
                        DropdownMenuItem(
                            text = { Text(instrument.name) },
                            onClick = {
                                onOptionsChanged(options.copy(instrument = instrument))
                                expandedInstrument = false
                            }
                        )
                    }
                }
            }
            
            // Difficulty Selector
            var expandedDifficulty by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedDifficulty,
                onExpandedChange = { expandedDifficulty = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = options.difficulty.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Difficulty") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDifficulty) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedDifficulty,
                    onDismissRequest = { expandedDifficulty = false }
                ) {
                    DifficultyLevel.values().forEach { difficulty ->
                        DropdownMenuItem(
                            text = { Text(difficulty.name) },
                            onClick = {
                                onOptionsChanged(options.copy(difficulty = difficulty))
                                expandedDifficulty = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FretboardConstraintsSection(
    options: CustomizationOptions,
    onOptionsChanged: (CustomizationOptions) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Fretboard Range",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Frets:", style = MaterialTheme.typography.bodyMedium)
            
            OutlinedTextField(
                value = options.minFret.toString(),
                onValueChange = { 
                    it.toIntOrNull()?.let { minFret ->
                        onOptionsChanged(options.copy(minFret = minFret))
                    }
                },
                label = { Text("Min") },
                modifier = Modifier.weight(1f)
            )
            
            Text("-", style = MaterialTheme.typography.bodyMedium)
            
            OutlinedTextField(
                value = options.maxFret.toString(),
                onValueChange = { 
                    it.toIntOrNull()?.let { maxFret ->
                        onOptionsChanged(options.copy(maxFret = maxFret))
                    }
                },
                label = { Text("Max") },
                modifier = Modifier.weight(1f)
            )
        }
        
        // Note Count
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Note Count: ${options.noteCount}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Slider(
                value = options.noteCount.toFloat(),
                onValueChange = { 
                    onOptionsChanged(options.copy(noteCount = it.toInt()))
                },
                valueRange = 4f..32f,
                steps = 27,
                modifier = Modifier.weight(2f)
            )
        }
    }
}

@Composable
private fun MusicTheorySection(
    options: CustomizationOptions,
    onOptionsChanged: (CustomizationOptions) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Music Theory",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Keys
        Text("Keys:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 4.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")) { key ->
                FilterChip(
                    onClick = { 
                        val newKeys = if (options.keys.contains(key)) {
                            options.keys - key
                        } else {
                            options.keys + key
                        }
                        onOptionsChanged(options.copy(keys = newKeys))
                    },
                    label = { Text(key) },
                    selected = options.keys.contains(key)
                )
            }
        }
        
        // Scales
        Text("Scales:", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 4.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(listOf("major", "minor", "pentatonic", "blues", "dorian", "mixolydian")) { scale ->
                FilterChip(
                    onClick = { 
                        val newScales = if (options.scales.contains(scale)) {
                            options.scales - scale
                        } else {
                            options.scales + scale
                        }
                        onOptionsChanged(options.copy(scales = newScales))
                    },
                    label = { Text(scale) },
                    selected = options.scales.contains(scale)
                )
            }
        }
    }
}

@Composable
private fun PlaybackSettingsSection(
    options: CustomizationOptions,
    onOptionsChanged: (CustomizationOptions) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Playback Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "BPM: ${options.bpm}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Slider(
                value = options.bpm.toFloat(),
                onValueChange = { 
                    onOptionsChanged(options.copy(bpm = it.toInt()))
                },
                valueRange = 60f..200f,
                steps = 139,
                modifier = Modifier.weight(2f)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = options.loop,
                    onCheckedChange = { onOptionsChanged(options.copy(loop = it)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Loop", style = MaterialTheme.typography.bodyMedium)
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = options.metronome,
                    onCheckedChange = { onOptionsChanged(options.copy(metronome = it)) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Metronome", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseCustomizationPanelPreview() {
    MaterialTheme {
        ExerciseCustomizationPanel(
            options = CustomizationOptions(),
            onOptionsChanged = {},
            onCreateExercise = {}
        )
    }
}