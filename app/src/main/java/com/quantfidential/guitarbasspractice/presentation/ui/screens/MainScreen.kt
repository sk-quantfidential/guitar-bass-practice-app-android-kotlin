package com.quantfidential.guitarbasspractice.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.quantfidential.guitarbasspractice.domain.model.*
import com.quantfidential.guitarbasspractice.domain.usecase.*
import com.quantfidential.guitarbasspractice.presentation.ui.components.*
import com.quantfidential.guitarbasspractice.presentation.viewmodel.MainViewModel
import androidx.compose.ui.graphics.Color
import com.quantfidential.guitarbasspractice.util.FretPosition as UtilFretPosition
import com.quantfidential.guitarbasspractice.presentation.viewmodel.MainUiState
import com.quantfidential.guitarbasspractice.presentation.viewmodel.MainEvent
import com.quantfidential.guitarbasspractice.util.OfflineCapabilities

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    @Suppress("UNUSED_PARAMETER") navController: NavController = rememberNavController(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Guitar & Bass Practice") },
                actions = {
                    IconButton(
                        onClick = { /* TODO: Settings */ }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            if (playbackState.isPlaying || uiState.currentExercise != null) {
                ExercisePlayerControls(
                    playbackState = playbackState,
                    onEvent = viewModel::handlePlaybackEvent
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Offline mode indicator
            OfflineModeIndicator(
                isOnline = isOnline,
                capabilities = uiState.offlineCapabilities
            )
            
            // Error message display
            uiState.errorMessage?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.handleEvent(MainEvent.ClearError) }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
            
            // User Profile Selector
            UserProfileSelector(
                profiles = uiState.profiles,
                activeProfile = uiState.activeProfile,
                onProfileSelected = viewModel::setActiveProfile,
                onAddProfile = viewModel::showCreateProfileDialog
            )
            
            // Current Exercise Display
            uiState.currentExercise?.let { exercise ->
                CurrentExerciseSection(
                    exercise = exercise,
                    playbackState = playbackState,
                    onPlaybackEvent = viewModel::handlePlaybackEvent
                )
            }
            
            // Main Content Tabs
            MainContentTabs(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::setSelectedTab,
                uiState = uiState,
                onEvent = viewModel::handleEvent
            )
        }
    }
    
    // Dialogs
    if (uiState.showCreateProfileDialog) {
        CreateProfileDialog(
            onDismiss = viewModel::hideCreateProfileDialog,
            onCreateProfile = viewModel::createProfile
        )
    }
}

@Composable
private fun CurrentExerciseSection(
    exercise: Exercise,
    playbackState: ExercisePlaybackState,
    @Suppress("UNUSED_PARAMETER") onPlaybackEvent: (ExerciseEngineEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = exercise.title,
                style = MaterialTheme.typography.headlineSmall
            )
            
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Fretboard Visualizer
            FretboardVisualizer(
                instrument = exercise.instrument,
                minFret = exercise.fretboard.minFret,
                maxFret = exercise.fretboard.maxFret,
                highlightedPositions = playbackState.highlightedPositions.map { utilPos ->
                    FretPosition(
                        string = utilPos.string,
                        fret = utilPos.fret,
                        note = utilPos.note,
                        isHighlighted = utilPos.isHighlighted,
                        highlightColor = Color.Blue
                    )
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Notation Renderer
            val tabNotes = exercise.notes.map { note ->
                TabNote(
                    string = note.stringNumber,
                    fret = note.fret,
                    beat = note.beat.toFloat(),
                    duration = note.duration.toFloat(),
                    isHighlighted = playbackState.highlightedPositions.any { 
                        it.string == note.stringNumber && it.fret == note.fret 
                    }
                )
            }
            
            NotationRenderer(
                notationType = NotationType.TAB,
                notes = tabNotes,
                instrument = exercise.instrument,
                currentBeat = playbackState.currentBeat
            )
        }
    }
}

@Composable
private fun MainContentTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    uiState: MainUiState,
    onEvent: (MainEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                icon = { Icon(Icons.Default.LibraryMusic, contentDescription = null) },
                text = { Text("Library") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Create") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { onTabSelected(2) },
                icon = { Icon(Icons.Default.Psychology, contentDescription = null) },
                text = { Text("AI") }
            )
        }
        
        when (selectedTab) {
            0 -> ExerciseLibraryTab(
                exercises = uiState.exercises,
                onExerciseSelected = { onEvent(MainEvent.SelectExercise(it)) }
            )
            1 -> ExerciseCreationTab(
                customizationOptions = uiState.customizationOptions,
                onOptionsChanged = { onEvent(MainEvent.UpdateCustomizationOptions(it)) },
                onCreateExercise = { onEvent(MainEvent.CreateCustomExercise()) }
            )
            2 -> AIExerciseTab(
                generationResult = uiState.aiGenerationResult,
                context = uiState.aiContext,
                suggestedPrompts = uiState.suggestedPrompts,
                isOnline = uiState.offlineCapabilities.aiGeneration,
                onContextChanged = { onEvent(MainEvent.UpdateAIContext(it)) },
                onGenerateExercise = { prompt, context -> onEvent(MainEvent.GenerateAIExercise(prompt, context)) }
            )
        }
    }
}

@Composable
private fun ExerciseLibraryTab(
    exercises: List<Exercise>,
    onExerciseSelected: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(exercises) { exercise ->
            ExerciseCard(
                exercise = exercise,
                onClick = { onExerciseSelected(exercise) }
            )
        }
    }
}

@Composable
private fun ExerciseCreationTab(
    customizationOptions: CustomizationOptions,
    onOptionsChanged: (CustomizationOptions) -> Unit,
    onCreateExercise: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        item {
            ExerciseCustomizationPanel(
                options = customizationOptions,
                onOptionsChanged = onOptionsChanged,
                onCreateExercise = onCreateExercise
            )
        }
    }
}

@Composable
private fun AIExerciseTab(
    generationResult: AIGenerationResult?,
    context: AIPromptContext,
    suggestedPrompts: List<String>,
    isOnline: Boolean,
    onContextChanged: (AIPromptContext) -> Unit,
    onGenerateExercise: (String, AIPromptContext) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        item {
            if (!isOnline) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Icon(
                            Icons.Default.CloudOff,
                            contentDescription = "Offline",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI features require internet connection",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            
            AIExerciseCreator(
                generationResult = generationResult,
                context = context,
                suggestedPrompts = suggestedPrompts,
                onContextChanged = onContextChanged,
                onGenerateExercise = onGenerateExercise
            )
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = exercise.title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = exercise.difficulty.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Row {
                Text(
                    text = exercise.instrument.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (exercise.isAiGenerated) {
                    Spacer(modifier = Modifier.width(8.dp))
                    AssistChip(
                        onClick = { },
                        label = { Text("AI", style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = "AI Generated",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        // Preview placeholder
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Main Screen Preview")
        }
    }
}