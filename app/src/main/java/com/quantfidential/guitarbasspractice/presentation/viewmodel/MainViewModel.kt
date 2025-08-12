package com.quantfidential.guitarbasspractice.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quantfidential.guitarbasspractice.domain.model.*
import com.quantfidential.guitarbasspractice.domain.repository.*
import com.quantfidential.guitarbasspractice.domain.usecase.*
import com.quantfidential.guitarbasspractice.util.NetworkConnectivity
import com.quantfidential.guitarbasspractice.util.OfflineCapabilities
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = false,
    val currentExercise: Exercise? = null,
    val exercises: List<Exercise> = emptyList(),
    val profiles: List<UserProfile> = emptyList(),
    val activeProfile: UserProfile? = null,
    val selectedTab: Int = 0,
    val customizationOptions: CustomizationOptions = CustomizationOptions(),
    val aiContext: AIPromptContext = AIPromptContext(),
    val aiGenerationResult: AIGenerationResult? = null,
    val suggestedPrompts: List<String> = emptyList(),
    val offlineCapabilities: OfflineCapabilities = OfflineCapabilities(),
    val showCreateProfileDialog: Boolean = false,
    val errorMessage: String? = null
)

sealed class MainEvent {
    data class SelectExercise(val exercise: Exercise) : MainEvent()
    data class CreateCustomExercise(val options: CustomizationOptions? = null) : MainEvent()
    data class GenerateAIExercise(val prompt: String, val context: AIPromptContext) : MainEvent()
    data class UpdateCustomizationOptions(val options: CustomizationOptions) : MainEvent()
    data class UpdateAIContext(val context: AIPromptContext) : MainEvent()
    data class SetActiveProfile(val profile: UserProfile) : MainEvent()
    data class CreateProfile(val name: String, val instrument: InstrumentType, val skillLevel: DifficultyLevel, val genres: List<String>) : MainEvent()
    data class SetSelectedTab(val tab: Int) : MainEvent()
    object ShowCreateProfileDialog : MainEvent()
    object HideCreateProfileDialog : MainEvent()
    object ClearError : MainEvent()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val userProfileRepository: UserProfileRepository,
    private val exerciseEngine: ExerciseEngine,
    private val customizationEngine: ExerciseCustomizationEngine,
    private val aiComposerAgent: AIComposerAgent,
    private val networkConnectivity: NetworkConnectivity
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _playbackState = MutableStateFlow(ExercisePlaybackState())
    val playbackState: StateFlow<ExercisePlaybackState> = _playbackState.asStateFlow()

    val isOnline: StateFlow<Boolean> = networkConnectivity.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        loadInitialData()
        observeNetworkState()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Load user profiles
            userProfileRepository.getAllActiveProfiles().collect { profiles ->
                val activeProfile = userProfileRepository.getActiveProfile()
                _uiState.value = _uiState.value.copy(
                    profiles = profiles,
                    activeProfile = activeProfile
                )
                
                // Load suggested prompts based on active profile
                activeProfile?.let { profile ->
                    val prompts = aiComposerAgent.getSuggestedPrompts(
                        profile.primaryInstrument, 
                        profile.skillLevel
                    )
                    _uiState.value = _uiState.value.copy(suggestedPrompts = prompts)
                }
            }
        }

        viewModelScope.launch {
            // Load exercises
            exerciseRepository.getAllExercises().collect { exercises ->
                _uiState.value = _uiState.value.copy(exercises = exercises)
            }
        }
    }

    private fun observeNetworkState() {
        viewModelScope.launch {
            isOnline.collect { online ->
                _uiState.value = _uiState.value.copy(
                    offlineCapabilities = OfflineCapabilities(
                        aiGeneration = online,
                        cloudSync = online,
                        onlineFeatures = online
                    )
                )
            }
        }
    }

    fun handleEvent(event: MainEvent) {
        when (event) {
            is MainEvent.SelectExercise -> selectExercise(event.exercise)
            is MainEvent.CreateCustomExercise -> createCustomExercise(event.options)
            is MainEvent.GenerateAIExercise -> generateAIExercise(event.prompt, event.context)
            is MainEvent.UpdateCustomizationOptions -> updateCustomizationOptions(event.options)
            is MainEvent.UpdateAIContext -> updateAIContext(event.context)
            is MainEvent.SetActiveProfile -> setActiveProfile(event.profile)
            is MainEvent.CreateProfile -> createProfile(event.name, event.instrument, event.skillLevel, event.genres)
            is MainEvent.SetSelectedTab -> setSelectedTab(event.tab)
            is MainEvent.ShowCreateProfileDialog -> showCreateProfileDialog()
            is MainEvent.HideCreateProfileDialog -> hideCreateProfileDialog()
            is MainEvent.ClearError -> clearError()
        }
    }

    fun handlePlaybackEvent(event: ExerciseEngineEvent) {
        val currentState = _playbackState.value
        val newState = exerciseEngine.handleEvent(event, currentState)
        _playbackState.value = newState

        // Start exercise execution if play is pressed
        if (event is ExerciseEngineEvent.Play && _uiState.value.currentExercise != null) {
            viewModelScope.launch {
                try {
                    exerciseEngine.executeExercise(
                        exercise = _uiState.value.currentExercise!!,
                        initialState = newState
                    ).collect { state ->
                        _playbackState.value = state
                    }
                } catch (e: Exception) {
                    // Handle exercise execution errors gracefully
                    _playbackState.value = newState.copy(isPlaying = false)
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Playback error: ${e.message}"
                    )
                }
            }
        }
    }

    private fun selectExercise(exercise: Exercise) {
        _uiState.value = _uiState.value.copy(currentExercise = exercise)
        // Reset playback state
        _playbackState.value = ExercisePlaybackState()
    }

    private fun createCustomExercise(options: CustomizationOptions?) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                
                val customizedOptions = options ?: _uiState.value.customizationOptions
                val exercise = customizationEngine.createCustomExercise(customizedOptions)
                
                exerciseRepository.insertExercise(exercise)
                selectExercise(exercise)
                
                _uiState.value = _uiState.value.copy(isLoading = false)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to create exercise: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }
    

    private fun generateAIExercise(prompt: String, context: AIPromptContext) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    aiGenerationResult = AIGenerationResult.Loading
                )
                
                val exercise = aiComposerAgent.generateExercise(prompt, context)
                
                exerciseRepository.insertExercise(exercise)
                selectExercise(exercise)
                
                _uiState.value = _uiState.value.copy(
                    aiGenerationResult = AIGenerationResult.Success(exercise)
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    aiGenerationResult = AIGenerationResult.Error("AI generation failed: ${e.message ?: "Unknown error"}")
                )
            }
        }
    }

    private fun updateCustomizationOptions(options: CustomizationOptions) {
        _uiState.value = _uiState.value.copy(customizationOptions = options)
    }

    private fun updateAIContext(context: AIPromptContext) {
        _uiState.value = _uiState.value.copy(aiContext = context)
    }

    fun setActiveProfile(profile: UserProfile) {
        viewModelScope.launch {
            userProfileRepository.setActiveProfile(profile.id)
            
            // Update suggested prompts
            val prompts = aiComposerAgent.getSuggestedPrompts(
                profile.primaryInstrument, 
                profile.skillLevel
            )
            _uiState.value = _uiState.value.copy(
                activeProfile = profile,
                suggestedPrompts = prompts
            )
        }
    }

    fun createProfile(name: String, instrument: InstrumentType, skillLevel: DifficultyLevel, genres: List<String>) {
        viewModelScope.launch {
            val profile = UserProfile(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                primaryInstrument = instrument,
                skillLevel = skillLevel,
                preferredKeys = listOf("C", "G", "D"),
                favoriteGenres = genres,
                createdTimestamp = System.currentTimeMillis(),
                isActive = true
            )
            
            userProfileRepository.insertProfile(profile)
            setActiveProfile(profile)
        }
    }

    fun setSelectedTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun showCreateProfileDialog() {
        _uiState.value = _uiState.value.copy(showCreateProfileDialog = true)
    }

    fun hideCreateProfileDialog() {
        _uiState.value = _uiState.value.copy(showCreateProfileDialog = false)
    }
    
    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}