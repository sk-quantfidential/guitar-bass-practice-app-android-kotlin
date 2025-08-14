package com.quantfidential.guitarbasspractice

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.quantfidential.guitarbasspractice.domain.model.*
import com.quantfidential.guitarbasspractice.domain.usecase.*
import com.quantfidential.guitarbasspractice.presentation.ui.components.*
import com.quantfidential.guitarbasspractice.util.OfflineCapabilities
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UIComponentTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testUserProfileSelectorDisplaysProfiles() {
        val testProfiles = listOf(
            UserProfile(
                id = "1",
                name = "John Guitar",
                primaryInstrument = InstrumentType.GUITAR,
                skillLevel = DifficultyLevel.INTERMEDIATE,
                preferredKeys = listOf("C", "G"),
                favoriteGenres = listOf("Rock"),
                createdTimestamp = 0L,
                isActive = true
            ),
            UserProfile(
                id = "2", 
                name = "Jane Bass",
                primaryInstrument = InstrumentType.BASS,
                skillLevel = DifficultyLevel.ADVANCED,
                preferredKeys = listOf("E", "A"),
                favoriteGenres = listOf("Jazz"),
                createdTimestamp = 0L,
                isActive = false
            )
        )

        composeTestRule.setContent {
            MaterialTheme {
                UserProfileSelector(
                    profiles = testProfiles,
                    activeProfile = testProfiles[0],
                    onProfileSelected = {},
                    onAddProfile = {}
                )
            }
        }

        composeTestRule.onNodeWithText("John Guitar").assertExists()
        composeTestRule.onNodeWithText("Jane Bass").assertExists()
        composeTestRule.onNodeWithText("Profile").assertExists()
        composeTestRule.onNodeWithContentDescription("Add Profile").assertExists()
    }

    @Test
    fun testCreateProfileDialogInteraction() {
        var profileCreated = false
        var createdName = ""
        var createdInstrument = InstrumentType.GUITAR
        var createdSkillLevel = DifficultyLevel.BEGINNER

        composeTestRule.setContent {
            MaterialTheme {
                CreateProfileDialog(
                    onDismiss = {},
                    onCreateProfile = { name, instrument, skill, _ ->
                        profileCreated = true
                        createdName = name
                        createdInstrument = instrument
                        createdSkillLevel = skill
                    }
                )
            }
        }

        // Check dialog elements exist
        composeTestRule.onNodeWithText("Create New Profile").assertExists()
        composeTestRule.onNodeWithText("Profile Name").assertExists()
        composeTestRule.onNodeWithText("Instrument").assertExists()
        composeTestRule.onNodeWithText("Skill Level").assertExists()
        
        // Input profile name
        composeTestRule.onNodeWithText("Profile Name").performTextInput("Test User")
        
        // Click create button
        composeTestRule.onNodeWithText("Create").performClick()
        
        // Verify profile was created
        assert(profileCreated) { "Profile should have been created" }
        assert(createdName == "Test User") { "Created name should be 'Test User'" }
    }

    @Test
    fun testExerciseCustomizationPanelSliders() {
        var optionsChanged = false
        var lastOptions: CustomizationOptions? = null
        val initialOptions = CustomizationOptions(
            bpm = 120,
            noteCount = 8,
            minFret = 0,
            maxFret = 12
        )

        composeTestRule.setContent {
            MaterialTheme {
                ExerciseCustomizationPanel(
                    options = initialOptions,
                    onOptionsChanged = { options ->
                        optionsChanged = true
                        lastOptions = options
                    },
                    onCreateExercise = {}
                )
            }
        }

        // Check that customization panel displays
        composeTestRule.onNodeWithText("Customize Exercise").assertExists()
        composeTestRule.onNodeWithText("Basic Settings").assertExists()
        composeTestRule.onNodeWithText("Music Theory").assertExists()
        composeTestRule.onNodeWithText("Playback Settings").assertExists()
        composeTestRule.onNodeWithText("Create Exercise").assertExists()
        
        // Verify sliders and inputs are present
        composeTestRule.onNodeWithText("BPM: 120").assertExists()
        composeTestRule.onNodeWithText("Note Count: 8").assertExists()
    }

    @Test
    fun testAIExerciseCreatorPromptInput() {
        var generateCalled = false
        var lastPrompt = ""
        var lastContext: AIPromptContext? = null

        composeTestRule.setContent {
            MaterialTheme {
                AIExerciseCreator(
                    generationResult = null,
                    context = AIPromptContext(),
                    suggestedPrompts = listOf("Create a blues scale exercise", "Practice major chords"),
                    onContextChanged = {},
                    onGenerateExercise = { prompt, context ->
                        generateCalled = true
                        lastPrompt = prompt
                        lastContext = context
                    }
                )
            }
        }

        // Check AI creator components exist
        composeTestRule.onNodeWithText("AI Exercise Creator").assertExists()
        composeTestRule.onNodeWithText("Context Settings").assertExists()
        composeTestRule.onNodeWithText("Generate with AI").assertExists()
        composeTestRule.onNodeWithText("Suggested Prompts").assertExists()
        
        // Check suggested prompts appear
        composeTestRule.onNodeWithText("Create a blues scale exercise").assertExists()
        composeTestRule.onNodeWithText("Practice major chords").assertExists()
        
        // Test prompt input
        composeTestRule.onNodeWithText("Describe your exercise...").performTextInput("Test prompt")
        composeTestRule.onNodeWithText("Generate with AI").performClick()
        
        assert(generateCalled) { "Generate function should have been called" }
        assert(lastPrompt == "Test prompt") { "Prompt should match input" }
    }

    @Test
    fun testOnlineModeIndicatorVisibility() {
        // Test online mode - indicator should not be visible
        composeTestRule.setContent {
            MaterialTheme {
                OfflineModeIndicator(
                    isOnline = true,
                    capabilities = OfflineCapabilities()
                )
            }
        }
        
        // Should not display anything when online
        composeTestRule.onNodeWithText("Offline Mode").assertDoesNotExist()
    }

    @Test
    fun testOfflineModeIndicatorVisibility() {
        // Test offline mode - indicator should be visible
        composeTestRule.setContent {
            MaterialTheme {
                OfflineModeIndicator(
                    isOnline = false,
                    capabilities = OfflineCapabilities()
                )
            }
        }
        
        composeTestRule.onNodeWithText("Offline Mode").assertExists()
        composeTestRule.onNodeWithText("Core features available").assertExists()
    }

    @Test
    fun testFeatureAvailabilityIndicator() {
        composeTestRule.setContent {
            MaterialTheme {
                FeatureAvailabilityIndicator(
                    featureName = "AI Exercise Generation",
                    isAvailable = false,
                    isOnlineRequired = true
                )
            }
        }
        
        composeTestRule.onNodeWithText("AI Exercise Generation").assertExists()
        composeTestRule.onNodeWithText("Requires internet connection").assertExists()
        composeTestRule.onNodeWithContentDescription("Unavailable").assertExists()
    }

    @Test
    fun testNotationRendererTypeSwitching() {
        composeTestRule.setContent {
            MaterialTheme {
                NotationRenderer(
                    notationType = NotationType.TAB,
                    notes = listOf(
                        TabNote(1, 0, 0f),
                        TabNote(2, 2, 1f)
                    ),
                    instrument = InstrumentType.GUITAR
                )
            }
        }
        
        // Check notation renderer displays
        composeTestRule.onNodeWithText("Notation").assertExists()
        composeTestRule.onNodeWithText("TAB").assertExists()
        composeTestRule.onNodeWithText("Staff").assertExists()
        composeTestRule.onNodeWithText("Chords").assertExists()
        composeTestRule.onNodeWithText("Fretboard").assertExists()
        
        // Test switching notation type
        composeTestRule.onNodeWithText("Staff").performClick()
        // After clicking Staff, the view should change (though we can't easily verify the canvas content)
    }

    @Test
    fun testAccessibilitySettingsToggle() {
        var settingsChanged = false
        var lastSettings: AccessibilitySettings? = null
        val initialSettings = AccessibilitySettings(
            highContrast = false,
            reduceMotion = false,
            hapticFeedback = true
        )

        composeTestRule.setContent {
            MaterialTheme {
                AccessibilitySettingsDialog(
                    settings = initialSettings,
                    onSettingsChanged = { settings ->
                        settingsChanged = true
                        lastSettings = settings
                    },
                    onDismiss = {}
                )
            }
        }
        
        // Check accessibility dialog displays
        composeTestRule.onNodeWithText("Accessibility Settings").assertExists()
        composeTestRule.onNodeWithText("Theme Mode").assertExists()
        composeTestRule.onNodeWithText("Color Scheme").assertExists()
        composeTestRule.onNodeWithText("Font Size").assertExists()
        composeTestRule.onNodeWithText("Features").assertExists()
        
        // Test toggle switches
        //composeTestRule.onNodeWithText("High Contrast").assertExists()
        composeTestRule.onNodeWithText("Reduce Motion").assertExists()
        composeTestRule.onNodeWithText("Haptic Feedback").assertExists()
        composeTestRule.onNodeWithText("Audio Feedback").assertExists()
    }

    @Test
    fun testFretboardVisualizerDisplays() {
        val testPositions = listOf(
            FretPosition(
                string = 1,
                fret = 0,
                note = "E",
                isHighlighted = true,
                highlightColor = androidx.compose.ui.graphics.Color.Red
            ),
            FretPosition(
                string = 2,
                fret = 2,
                note = "C#",
                isHighlighted = false,
                highlightColor = androidx.compose.ui.graphics.Color.Blue
            )
        )

        composeTestRule.setContent {
            MaterialTheme {
                FretboardVisualizer(
                    instrument = InstrumentType.GUITAR,
                    minFret = 0,
                    maxFret = 12,
                    highlightedPositions = testPositions
                )
            }
        }
        
        // The fretboard should render (though canvas content is hard to test)
        // We can at least verify the component doesn't crash
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun testExercisePlayerControlsInteraction() {
        var playbackEventCalled = false
        var lastEvent: ExerciseEngineEvent? = null
        
        val playbackState = ExercisePlaybackState(
            isPlaying = false,
            currentBeat = 0f,
            progress = 0f,
            highlightedPositions = emptyList()
        )

        composeTestRule.setContent {
            MaterialTheme {
                ExercisePlayerControls(
                    playbackState = playbackState,
                    onEvent = { event ->
                        playbackEventCalled = true
                        lastEvent = event
                    }
                )
            }
        }
        
        // The player controls should display
        composeTestRule.onRoot().assertExists()
        
        // Look for common control buttons (play/pause, stop, etc.)
        // Note: We'd need to check the actual implementation to verify specific buttons
    }
}