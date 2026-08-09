package com.example

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppPreferences
import com.example.ui.screens.AvatarCreatorScreen
import com.example.ui.screens.GeminiPlaygroundScreen
import com.example.ui.screens.LlmGuideScreen
import com.example.ui.screens.ModelStudioScreen
import com.example.ui.screens.RoleplayChatScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VideoMakerScreen
import com.example.ui.theme.ChatThemePresets
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.AuraViewModel
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.SettingsViewModel
import java.util.Locale

/**
 * Enterprise Main Activity implementing 100% Jetpack Compose with Edge-to-Edge support.
 */
class MainActivity : ComponentActivity() {

    private val auraViewModel: AuraViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Android TextToSpeech engine for voice narration
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }

        setContent {
            MyApplicationTheme {
                AuraStudioApp(
                    auraViewModel = auraViewModel,
                    mainViewModel = mainViewModel,
                    settingsViewModel = settingsViewModel,
                    onSpeakText = { text -> speakText(text) }
                )
            }
        }
    }

    private fun speakText(text: String) {
        val cleanText = text.replace(Regex("\\*.*?\\*"), "") // remove action asterisks for audio
        if (cleanText.isNotBlank()) {
            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "aura_tts_id")
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}

enum class AuraTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    STUDIO("Studio", Icons.Default.AutoAwesome),
    AVATAR("Avatar", Icons.Default.Face),
    GEMINI("Gemini", Icons.Default.Terminal),
    CHAT("Chat", Icons.Default.ChatBubble),
    VIDEO("Video", Icons.Default.Movie),
    GUIDE("Guide", Icons.Default.Psychology),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun AuraStudioApp(
    auraViewModel: AuraViewModel,
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel = viewModel(),
    onSpeakText: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(AuraTab.STUDIO) }
    val context = LocalContext.current

    val appPreferences by settingsViewModel.appPreferences.collectAsStateWithLifecycle()
    val avatarTraits by settingsViewModel.avatarTraits.collectAsStateWithLifecycle()

    val personas by auraViewModel.personas.collectAsStateWithLifecycle()
    val selectedPersona by auraViewModel.selectedPersona.collectAsStateWithLifecycle()
    val chatMessages by auraViewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatGenerating by auraViewModel.isChatGenerating.collectAsStateWithLifecycle()
    val selectedChatThemeId by auraViewModel.selectedChatThemeId.collectAsStateWithLifecycle()
    val autoThemeMode by auraViewModel.autoThemeMode.collectAsStateWithLifecycle()

    val currentChatTheme = remember(selectedChatThemeId) {
        ChatThemePresets.getThemeById(selectedChatThemeId)
    }

    val videoProjects by auraViewModel.videoProjects.collectAsStateWithLifecycle()
    val activeVideoProject by auraViewModel.activeVideoProject.collectAsStateWithLifecycle()
    val parsedScenes by auraViewModel.parsedScenes.collectAsStateWithLifecycle()
    val isVideoGenerating by auraViewModel.isVideoGenerating.collectAsStateWithLifecycle()
    val isAudioPlaying by auraViewModel.isAudioPlaying.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkObsidian,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                contentColor = TextPrimary,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("aura_bottom_nav_bar")
            ) {
                AuraTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (isSelected) NeonMagenta else TextMuted
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) NeonMagenta else TextMuted,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = NeonMagenta.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            when (selectedTab) {
                AuraTab.STUDIO -> {
                    ModelStudioScreen(
                        personas = personas,
                        onSelectPersonaForChat = { persona ->
                            auraViewModel.selectPersona(persona)
                            selectedTab = AuraTab.CHAT
                        },
                        onSelectPersonaForVideo = { persona ->
                            auraViewModel.selectPersona(persona)
                            selectedTab = AuraTab.VIDEO
                        },
                        onGenerateAvatarImage = { name, age, title, category, personality, backstory ->
                            auraViewModel.generatePersonaAvatarImage(
                                name = name,
                                age = age,
                                title = title,
                                category = category,
                                personality = personality,
                                backstory = backstory
                            )
                        },
                        onCreateCustomModel = { name, title, age, category, personality, backstory, scenario, voiceStyle, systemPrompt, creativity, avatarImageUrl, avatarPrompt ->
                            auraViewModel.createCustomPersona(
                                name, title, age, category, personality, backstory, scenario, voiceStyle, systemPrompt, creativity, avatarImageUrl, avatarPrompt
                            )
                            Toast.makeText(context, "Character '$name' saved to Room Database!", Toast.LENGTH_SHORT).show()
                        },
                        onDeleteCustomModel = { id ->
                            auraViewModel.deletePersona(id)
                            Toast.makeText(context, "Custom model deleted", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                AuraTab.AVATAR -> {
                    AvatarCreatorScreen(
                        persistedTraits = avatarTraits,
                        settingsViewModel = settingsViewModel
                    )
                }

                AuraTab.GEMINI -> {
                    GeminiPlaygroundScreen(
                        mainViewModel = mainViewModel,
                        onSpeakText = onSpeakText
                    )
                }

                AuraTab.CHAT -> {
                    RoleplayChatScreen(
                        persona = selectedPersona ?: personas.firstOrNull(),
                        allPersonas = personas,
                        messages = chatMessages,
                        isGenerating = isChatGenerating,
                        chatTheme = currentChatTheme,
                        autoThemeMode = autoThemeMode,
                        onSendMessage = { text ->
                            if (selectedPersona == null && personas.isNotEmpty()) {
                                auraViewModel.selectPersona(personas.first())
                            }
                            auraViewModel.sendMessage(text)
                        },
                        onSelectPersona = { persona ->
                            auraViewModel.selectPersona(persona)
                            Toast.makeText(context, "Active character set to ${persona.name}", Toast.LENGTH_SHORT).show()
                        },
                        onCreateCustomModelRequested = {
                            selectedTab = AuraTab.STUDIO
                        },
                        onDeletePersona = { id ->
                            auraViewModel.deletePersona(id)
                            Toast.makeText(context, "Avatar deleted", Toast.LENGTH_SHORT).show()
                        },
                        onClearHistory = {
                            auraViewModel.clearChatHistory()
                            Toast.makeText(context, "Chat history cleared", Toast.LENGTH_SHORT).show()
                        },
                        onPlayAudio = { text ->
                            onSpeakText(text)
                        },
                        onSelectTheme = { theme ->
                            auraViewModel.setChatTheme(theme.id)
                            Toast.makeText(context, "Theme set to ${theme.name}", Toast.LENGTH_SHORT).show()
                        },
                        onToggleAutoTheme = { enabled ->
                            auraViewModel.setAutoThemeMode(enabled)
                            val statusMsg = if (enabled) "Auto-theme enabled" else "Manual theme control active"
                            Toast.makeText(context, statusMsg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                AuraTab.VIDEO -> {
                    VideoMakerScreen(
                        personas = personas,
                        selectedPersona = selectedPersona ?: personas.firstOrNull(),
                        videoProjects = videoProjects,
                        activeProject = activeVideoProject,
                        scenes = parsedScenes,
                        isGenerating = isVideoGenerating,
                        isAudioPlaying = isAudioPlaying,
                        onGenerateVideo = { title, personaId, prompt, style, sceneCount ->
                            auraViewModel.generateVideoScript(title, personaId, prompt, style, sceneCount)
                        },
                        onSelectProject = { proj ->
                            auraViewModel.setActiveProject(proj)
                        },
                        onToggleAudio = {
                            auraViewModel.toggleAudioNarration()
                            val currentNarration = parsedScenes.firstOrNull()?.narrationText ?: ""
                            if (currentNarration.isNotEmpty()) {
                                onSpeakText(currentNarration)
                            }
                        },
                        onPlaySceneAudio = { narration ->
                            onSpeakText(narration)
                        }
                    )
                }

                AuraTab.GUIDE -> {
                    LlmGuideScreen()
                }

                AuraTab.SETTINGS -> {
                    SettingsScreen(
                        appPreferences = appPreferences,
                        settingsViewModel = settingsViewModel,
                        onSpeakTestText = onSpeakText
                    )
                }
            }
        }
    }
}
