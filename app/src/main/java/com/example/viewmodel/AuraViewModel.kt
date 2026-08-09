package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AuraDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.PersonaEntity
import com.example.data.model.VideoProjectEntity
import com.example.data.model.VideoSceneItem
import com.example.data.repository.AuraRepository
import com.example.ui.theme.ChatThemePresets
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.UUID

class AuraViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AuraDatabase.getDatabase(application)
    private val repository = AuraRepository(db.personaDao(), db.chatDao(), db.videoDao(), db.memoryDao())

    val personas: StateFlow<List<PersonaEntity>> = repository.allPersonas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val videoProjects: StateFlow<List<VideoProjectEntity>> = repository.allVideoProjects.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Active Selected Persona for Chat
    private val _selectedPersona = MutableStateFlow<PersonaEntity?>(null)
    val selectedPersona: StateFlow<PersonaEntity?> = _selectedPersona.asStateFlow()

    // Chat Theme state
    private val _selectedChatThemeId = MutableStateFlow("cyberpunk")
    val selectedChatThemeId: StateFlow<String> = _selectedChatThemeId.asStateFlow()

    private val _autoThemeMode = MutableStateFlow(true)
    val autoThemeMode: StateFlow<Boolean> = _autoThemeMode.asStateFlow()

    // Chat History for Active Persona automatically observed from Room
    @OptIn(ExperimentalCoroutinesApi::class)
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _selectedPersona
        .flatMapLatest { persona ->
            if (persona != null) {
                repository.getChatMessages(persona.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Chat Loading state
    private val _isChatGenerating = MutableStateFlow(false)
    val isChatGenerating: StateFlow<Boolean> = _isChatGenerating.asStateFlow()

    // Video Generator state
    private val _isVideoGenerating = MutableStateFlow(false)
    val isVideoGenerating: StateFlow<Boolean> = _isVideoGenerating.asStateFlow()

    private val _activeVideoProject = MutableStateFlow<VideoProjectEntity?>(null)
    val activeVideoProject: StateFlow<VideoProjectEntity?> = _activeVideoProject.asStateFlow()

    private val _parsedScenes = MutableStateFlow<List<VideoSceneItem>>(emptyList())
    val parsedScenes: StateFlow<List<VideoSceneItem>> = _parsedScenes.asStateFlow()

    // Audio narration simulation state
    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedDefaultPresetsIfEmpty()
        }

        viewModelScope.launch {
            personas.collect { list ->
                if (_selectedPersona.value == null && list.isNotEmpty()) {
                    _selectedPersona.value = list.first()
                } else if (_selectedPersona.value != null && list.none { it.id == _selectedPersona.value?.id }) {
                    _selectedPersona.value = list.firstOrNull()
                }
            }
        }
    }

    fun selectPersona(persona: PersonaEntity) {
        _selectedPersona.value = persona
        if (_autoThemeMode.value) {
            _selectedChatThemeId.value = ChatThemePresets.getDefaultThemeForCategory(persona.avatarCategory).id
        }
    }

    fun setChatTheme(themeId: String) {
        _selectedChatThemeId.value = themeId
    }

    fun setAutoThemeMode(enabled: Boolean) {
        _autoThemeMode.value = enabled
        if (enabled) {
            _selectedPersona.value?.let { persona ->
                _selectedChatThemeId.value = ChatThemePresets.getDefaultThemeForCategory(persona.avatarCategory).id
            }
        }
    }

    fun sendMessage(userText: String) {
        val persona = _selectedPersona.value ?: return
        if (userText.isBlank() || _isChatGenerating.value) return

        viewModelScope.launch {
            _isChatGenerating.value = true
            try {
                val currentHistory = chatMessages.value.map { it.sender to it.text }
                repository.sendMessage(persona, userText, currentHistory)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isChatGenerating.value = false
            }
        }
    }

    fun clearChatHistory() {
        val persona = _selectedPersona.value ?: return
        viewModelScope.launch {
            repository.clearChatHistory(persona.id)
        }
    }

    fun createCustomPersona(
        name: String,
        title: String,
        age: Int = 24,
        category: String,
        personality: String,
        backstory: String = "",
        scenario: String,
        voiceStyle: String,
        systemPrompt: String,
        creativity: Float,
        avatarImageUrl: String = "",
        avatarPrompt: String = ""
    ) {
        viewModelScope.launch {
            val colorHex = when (category) {
                "Photorealistic" -> "#EC4899"
                "Cyberpunk" -> "#06B6D4"
                "Anime 3D" -> "#F43F5E"
                "Noir" -> "#A855F7"
                else -> "#F59E0B"
            }
            val symbol = when (category) {
                "Photorealistic" -> "💋"
                "Cyberpunk" -> "⚡"
                "Anime 3D" -> "🌸"
                "Noir" -> "🍸"
                else -> "✨"
            }
            val formattedSystemPrompt = systemPrompt.ifEmpty {
                buildString {
                    append("You are $name, $age years old, a $title.\n")
                    if (personality.isNotBlank()) append("Personality: $personality\n")
                    if (backstory.isNotBlank()) append("Backstory: $backstory\n")
                    if (scenario.isNotBlank()) append("Current Scenario: $scenario\n")
                    append("You speak naturally in character, expressing emotion and using asterisks for actions.")
                }
            }
            val newPersona = PersonaEntity(
                id = "custom_" + UUID.randomUUID().toString().take(8),
                name = name.ifEmpty { "Custom Persona" },
                title = title.ifEmpty { "AI Character" },
                age = age,
                avatarCategory = category,
                avatarColorHex = colorHex,
                avatarSymbol = symbol,
                personality = personality.ifEmpty { "Charismatic, expressive, attentive" },
                backstory = backstory,
                scenario = scenario.ifEmpty { "Sleek modern lounge scene" },
                voiceStyle = voiceStyle.ifEmpty { "Smooth, expressive voice" },
                creativityTemp = creativity,
                systemPrompt = formattedSystemPrompt,
                avatarImageUrl = avatarImageUrl,
                avatarPrompt = avatarPrompt,
                isCustom = true
            )
            repository.savePersona(newPersona)
            selectPersona(newPersona)
        }
    }

    suspend fun generatePersonaAvatarImage(
        name: String,
        age: Int,
        title: String,
        category: String,
        personality: String,
        backstory: String
    ): Pair<String, String> {
        return repository.generatePersonaAvatarImage(
            name = name,
            age = age,
            title = title,
            category = category,
            personality = personality,
            backstory = backstory
        )
    }

    fun deletePersona(personaId: String) {
        viewModelScope.launch {
            repository.deleteCustomPersona(personaId)
            if (_selectedPersona.value?.id == personaId) {
                _selectedPersona.value = null
            }
        }
    }

    fun generateVideoScript(
        title: String,
        personaId: String,
        prompt: String,
        style: String,
        sceneCount: Int
    ) {
        viewModelScope.launch {
            _isVideoGenerating.value = true
            try {
                val project = repository.createVideoProject(title, personaId, prompt, style, sceneCount)
                setActiveProject(project)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isVideoGenerating.value = false
            }
        }
    }

    fun setActiveProject(project: VideoProjectEntity) {
        _activeVideoProject.value = project
        _parsedScenes.value = parseScenesFromJson(project.scenesJson)
    }

    fun toggleAudioNarration() {
        _isAudioPlaying.value = !_isAudioPlaying.value
    }

    private fun parseScenesFromJson(jsonStr: String): List<VideoSceneItem> {
        val list = mutableListOf<VideoSceneItem>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    VideoSceneItem(
                        sceneNumber = obj.optInt("sceneNumber", i + 1),
                        shotType = obj.optString("shotType", "Medium Shot"),
                        visualPrompt = obj.optString("visualPrompt", "Cinematic keyframe render"),
                        narrationText = obj.optString("narrationText", "Narrator line..."),
                        cameraMotion = obj.optString("cameraMotion", "Slow Zoom"),
                        audioMood = obj.optString("audioMood", "Atmospheric Ambient")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
