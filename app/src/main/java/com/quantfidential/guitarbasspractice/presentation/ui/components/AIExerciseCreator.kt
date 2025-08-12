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
import androidx.compose.ui.unit.sp
import com.quantfidential.guitarbasspractice.domain.model.InstrumentType
import com.quantfidential.guitarbasspractice.domain.model.DifficultyLevel
import com.quantfidential.guitarbasspractice.domain.usecase.AIGenerationResult
import com.quantfidential.guitarbasspractice.domain.usecase.AIPromptContext

@Composable
fun AIExerciseCreator(
    generationResult: AIGenerationResult?,
    context: AIPromptContext,
    suggestedPrompts: List<String>,
    onContextChanged: (AIPromptContext) -> Unit,
    onGenerateExercise: (String, AIPromptContext) -> Unit,
    modifier: Modifier = Modifier
) {
    var prompt by remember { mutableStateOf("") }
    var showAdvancedSettings by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = "AI",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Exercise Creator",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Context Settings (collapsed by default)
            item {
                ContextSettingsSection(
                    context = context,
                    expanded = showAdvancedSettings,
                    onExpandedChange = { showAdvancedSettings = it },
                    onContextChanged = onContextChanged
                )
            }
            
            // Prompt Input
            item {
                PromptInputSection(
                    prompt = prompt,
                    onPromptChanged = { prompt = it },
                    onGenerate = { onGenerateExercise(prompt, context) },
                    isLoading = generationResult is AIGenerationResult.Loading
                )
            }
            
            // Suggested Prompts
            item {
                SuggestedPromptsSection(
                    prompts = suggestedPrompts,
                    onPromptSelected = { selectedPrompt ->
                        prompt = selectedPrompt
                        onGenerateExercise(selectedPrompt, context)
                    }
                )
            }
            
            // Generation Result
            generationResult?.let { result ->
                item {
                    GenerationResultSection(result = result)
                }
            }
        }
    }
}

@Composable
private fun ContextSettingsSection(
    context: AIPromptContext,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onContextChanged: (AIPromptContext) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Context Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                IconButton(
                    onClick = { onExpandedChange(!expanded) }
                ) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Quick context display
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        AssistChip(
                            onClick = { },
                            label = { Text(context.instrument.name) },
                            leadingIcon = {
                                Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                    item {
                        AssistChip(
                            onClick = { },
                            label = { Text(context.difficulty.name) },
                            leadingIcon = {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                    item {
                        AssistChip(
                            onClick = { },
                            label = { Text("Key: ${context.key}") },
                            leadingIcon = {
                                Icon(Icons.Default.Piano, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Detailed settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var expandedInstrument by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedInstrument,
                        onExpandedChange = { expandedInstrument = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = context.instrument.name,
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
                                        onContextChanged(context.copy(instrument = instrument))
                                        expandedInstrument = false
                                    }
                                )
                            }
                        }
                    }
                    
                    var expandedDifficulty by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedDifficulty,
                        onExpandedChange = { expandedDifficulty = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = context.difficulty.name,
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
                                        onContextChanged(context.copy(difficulty = difficulty))
                                        expandedDifficulty = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = context.key,
                        onValueChange = { onContextChanged(context.copy(key = it)) },
                        label = { Text("Key") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    OutlinedTextField(
                        value = context.genre,
                        onValueChange = { onContextChanged(context.copy(genre = it)) },
                        label = { Text("Genre") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PromptInputSection(
    prompt: String,
    onPromptChanged: (String) -> Unit,
    onGenerate: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = prompt,
            onValueChange = onPromptChanged,
            label = { Text("Describe your exercise...") },
            placeholder = { Text("e.g., Create a blues scale exercise in E minor") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        FilledTonalButton(
            onClick = onGenerate,
            enabled = prompt.isNotEmpty() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generating...")
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate with AI")
            }
        }
    }
}

@Composable
private fun SuggestedPromptsSection(
    prompts: List<String>,
    onPromptSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (prompts.isNotEmpty()) {
        Column(modifier = modifier) {
            Text(
                text = "Suggested Prompts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.height(120.dp)
            ) {
                items(prompts) { prompt ->
                    SuggestionChip(
                        onClick = { onPromptSelected(prompt) },
                        label = { Text(prompt, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun GenerationResultSection(
    result: AIGenerationResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = when (result) {
            is AIGenerationResult.Success -> CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
            is AIGenerationResult.Error -> CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
            is AIGenerationResult.Loading -> CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            when (result) {
                is AIGenerationResult.Success -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Exercise Generated Successfully!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = result.exercise.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = result.exercise.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                is AIGenerationResult.Error -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Generation Failed",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = result.message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                is AIGenerationResult.Loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Generating exercise with AI...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AIExerciseCreatorPreview() {
    MaterialTheme {
        AIExerciseCreator(
            generationResult = null,
            context = AIPromptContext(),
            suggestedPrompts = listOf(
                "Create a simple C major scale exercise",
                "Generate basic chord progression practice",
                "Make an exercise for learning open chords"
            ),
            onContextChanged = {},
            onGenerateExercise = { _, _ -> }
        )
    }
}