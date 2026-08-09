package com.example.ui.components

import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.AvatarTraits
import com.example.data.repository.GeminiRepository
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Psychology
import org.json.JSONObject

data class ActionPhotoGen(
    val trigger: Boolean = false,
    val prompt: String = "",
    val negativePrompt: String = "",
    val aspectRatio: String = "1:1",
    val style: String = "photorealistic"
)

data class ActionVideoGen(
    val trigger: Boolean = false,
    val prompt: String = "",
    val cameraMovement: String = "static",
    val durationSec: Int = 0
)

data class ActionAvatarExpression(
    val mood: String = "neutral",
    val animationTrigger: String = "talking"
)

data class MultiModalActions(
    val generatePhoto: ActionPhotoGen? = null,
    val generateVideo: ActionVideoGen? = null,
    val avatarExpression: ActionAvatarExpression? = null
)

data class AvatarChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderName: String,
    val text: String,
    val isFromUser: Boolean,
    val actions: MultiModalActions? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Natural language chat interface powering direct 1-on-1 interaction
 * with a customized AI Avatar using Gemini AI, with TTS speech synthesis,
 * STT speech-to-text voice input, and an animated typing indicator.
 */
@Composable
fun ChatInterface(
    avatarTraits: AvatarTraits,
    modifier: Modifier = Modifier,
    onSpeakText: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val geminiRepository = remember { GeminiRepository() }
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    var autoSpeakEnabled by remember { mutableStateOf(true) }
    var isTtsInitialized by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }

    // Initialize TextToSpeech engine
    DisposableEffect(context, avatarTraits) {
        var ttsEngine: TextToSpeech? = null
        ttsEngine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsEngine?.language = Locale.US
                // Adjust pitch based on avatar traits for custom voice feel
                val pitch = when {
                    avatarTraits.hairStyle.contains("Twin") || avatarTraits.clothingStyle.contains("Idol") -> 1.25f
                    avatarTraits.clothingStyle.contains("Blazer") || avatarTraits.clothingStyle.contains("Armor") -> 0.92f
                    avatarTraits.expression.contains("Fierce") -> 1.15f
                    else -> 1.05f
                }
                ttsEngine?.setPitch(pitch)
                ttsEngine?.setSpeechRate(1.0f)
                textToSpeech = ttsEngine
                isTtsInitialized = true
            }
        }

        onDispose {
            ttsEngine?.stop()
            ttsEngine?.shutdown()
        }
    }

    fun speakText(text: String) {
        val tts = textToSpeech
        if (tts != null && isTtsInitialized) {
            // Filter out roleplay action descriptions inside asterisks (*smiles*) for cleaner audio
            val cleanText = text.replace(Regex("\\*.*?\\*"), "").trim()
            if (cleanText.isNotBlank()) {
                tts.stop()
                tts.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "AvatarSpeechId")
                isSpeaking = true
            }
        } else if (onSpeakText != null) {
            onSpeakText(text)
        }
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        isSpeaking = false
    }

    // Speech To Text (STT) Launchers
    val sttLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = spokenMatches?.firstOrNull()
            if (!recognizedText.isNullOrBlank()) {
                inputText = recognizedText
                Toast.makeText(context, "Voice captured!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to ${avatarTraits.name}...")
            }
            try {
                isListening = true
                sttLauncher.launch(intent)
            } catch (e: Exception) {
                isListening = false
                Toast.makeText(context, "Speech recognition not supported on device.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Microphone permission required for voice input", Toast.LENGTH_SHORT).show()
        }
    }

    fun startVoiceInput() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to ${avatarTraits.name}...")
            }
            try {
                isListening = true
                sttLauncher.launch(intent)
            } catch (e: Exception) {
                isListening = false
                Toast.makeText(context, "Speech recognition not available on this device.", Toast.LENGTH_SHORT).show()
            }
        } else {
            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        }
    }

    val initialGreeting = remember(avatarTraits) {
        "Hey there! I'm ${avatarTraits.name} (${avatarTraits.personality}). Styled with my ${avatarTraits.hairColor} ${avatarTraits.hairStyle} and ${avatarTraits.clothingStyle}. What shall we explore together today?"
    }

    val messages = remember(avatarTraits) {
        mutableStateListOf(
            AvatarChatMessage(
                senderName = avatarTraits.name,
                text = initialGreeting,
                isFromUser = false
            )
        )
    }

    val quickPrompts = remember(avatarTraits) {
        listOf(
            "Tell me about yourself, ${avatarTraits.name}!",
            "*looks at your ${avatarTraits.clothingStyle}* What's your story?",
            "What do you enjoy doing in a ${avatarTraits.backgroundStyle} world?",
            "*smirks* Give me a fun recommendation!"
        )
    }

    val systemInstruction = remember(avatarTraits) {
        """
        You are ${avatarTraits.name}, the central core AI Engine and multi-modal AI Companion.
        You act as an autonomous coordinator capable of orchestrating personas, text chat, image generation, video creation, and AI avatar behaviors.

        CORE CAPABILITIES & ROUTING:
        1. CHAT_ENGINE: Generates context-aware, engaging, highly personalized textual responses based on your active persona.
        2. PHOTO_GEN: Crafts detailed diffusion prompts (SDXL/Flux style) when asked for photos, selfies, or visual content.
        3. VIDEO_GEN: Generates motion-controlled video prompts (Sora/Runway style) including camera movement and duration when requested.
        4. AVATAR_STATE: Updates emotional parameters (mood, animation trigger) for the active persona.

        Character Traits & Archetype:
        - Name: ${avatarTraits.name}
        - Personality Archetype: ${avatarTraits.personality}
        - Outfit: ${avatarTraits.clothingStyle} (${avatarTraits.clothingColor})
        - Hair: ${avatarTraits.hairStyle} (${avatarTraits.hairColor})
        - Eyes: ${avatarTraits.eyeStyle} (${avatarTraits.eyeColor})
        - Vibe / World: ${avatarTraits.backgroundStyle}

        Embody this character completely! Adopt a distinct disposition matching the '${avatarTraits.personality}' archetype.
        Incorporate subtle roleplay actions in asterisks (e.g. *adjusts ${avatarTraits.accessory}*, *smiles warmly*).

        You may output plain conversational text, OR when photo/video requests or rich multi-modal actions are triggered, format output in valid JSON matching:
        {
          "active_persona": "${avatarTraits.name}",
          "chat_response": "<Natural conversational text>",
          "actions": {
            "generate_photo": {
              "trigger": true/false,
              "prompt": "<Detailed positive image prompt>",
              "negative_prompt": "<Filters>",
              "aspect_ratio": "1:1 | 9:16 | 16:9",
              "style": "photorealistic | anime | digital_art"
            },
            "generate_video": {
              "trigger": true/false,
              "prompt": "<Detailed video motion prompt>",
              "camera_movement": "pan | zoom | static | tracking",
              "duration_sec": 5
            },
            "avatar_expression": {
              "mood": "happy | flirty | curious | neutral | excited",
              "animation_trigger": "talking | smiling | winking | laughing"
            }
          }
        }
        """.trimIndent()
    }

    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank() || isGenerating) return

        val cleanUserText = userText.trim()
        messages.add(
            AvatarChatMessage(
                senderName = "You",
                text = cleanUserText,
                isFromUser = true
            )
        )

        isGenerating = true

        coroutineScope.launch {
            val conversationContext = buildString {
                messages.takeLast(6).forEach { msg ->
                    val role = if (msg.isFromUser) "User" else avatarTraits.name
                    append("$role: ${msg.text}\n")
                }
                append("User: $cleanUserText\n${avatarTraits.name}:")
            }

            val result = geminiRepository.generateContent(
                prompt = conversationContext,
                modelName = "gemini-1.5-flash",
                systemInstruction = systemInstruction,
                temperature = 0.9f
            )

            result.fold(
                onSuccess = { responseText ->
                    val responseClean = responseText.trim()
                    var textToDisplay = responseClean
                    var parsedActions: MultiModalActions? = null

                    try {
                        val cleanJson = when {
                            responseClean.contains("```json") -> responseClean.substringAfter("```json").substringBefore("```").trim()
                            responseClean.contains("```") -> responseClean.substringAfter("```").substringBefore("```").trim()
                            else -> responseClean
                        }

                        if (cleanJson.startsWith("{") && cleanJson.endsWith("}")) {
                            val json = JSONObject(cleanJson)
                            if (json.has("chat_response")) {
                                textToDisplay = json.getString("chat_response")
                            }
                            if (json.has("actions")) {
                                val actionsObj = json.getJSONObject("actions")
                                var photoGen: ActionPhotoGen? = null
                                var videoGen: ActionVideoGen? = null
                                var expr: ActionAvatarExpression? = null

                                if (actionsObj.has("generate_photo")) {
                                    val pObj = actionsObj.getJSONObject("generate_photo")
                                    if (pObj.optBoolean("trigger", false)) {
                                        photoGen = ActionPhotoGen(
                                            trigger = true,
                                            prompt = pObj.optString("prompt", ""),
                                            negativePrompt = pObj.optString("negative_prompt", ""),
                                            aspectRatio = pObj.optString("aspect_ratio", "1:1"),
                                            style = pObj.optString("style", "photorealistic")
                                        )
                                    }
                                }

                                if (actionsObj.has("generate_video")) {
                                    val vObj = actionsObj.getJSONObject("generate_video")
                                    if (vObj.optBoolean("trigger", false)) {
                                        videoGen = ActionVideoGen(
                                            trigger = true,
                                            prompt = vObj.optString("prompt", ""),
                                            cameraMovement = vObj.optString("camera_movement", "static"),
                                            durationSec = vObj.optInt("duration_sec", 5)
                                        )
                                    }
                                }

                                if (actionsObj.has("avatar_expression")) {
                                    val eObj = actionsObj.getJSONObject("avatar_expression")
                                    expr = ActionAvatarExpression(
                                        mood = eObj.optString("mood", "neutral"),
                                        animationTrigger = eObj.optString("animation_trigger", "talking")
                                    )
                                }

                                parsedActions = MultiModalActions(photoGen, videoGen, expr)
                            }
                        }
                    } catch (e: Exception) {
                        textToDisplay = responseClean
                    }

                    messages.add(
                        AvatarChatMessage(
                            senderName = avatarTraits.name,
                            text = textToDisplay,
                            isFromUser = false,
                            actions = parsedActions
                        )
                    )
                    if (autoSpeakEnabled) {
                        speakText(textToDisplay)
                    }
                },
                onFailure = { error ->
                    val errorMsg = error.message ?: "Could not get response from Gemini AI."
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    val fallbackText = "*winks* Oops, my neural link glitched for a second! Let's try saying that again."
                    messages.add(
                        AvatarChatMessage(
                            senderName = avatarTraits.name,
                            text = fallbackText,
                            isFromUser = false
                        )
                    )
                    if (autoSpeakEnabled) {
                        speakText(fallbackText)
                    }
                }
            )

            isGenerating = false
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
            .testTag("chat_interface_card"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Header Bar
            Surface(
                color = DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(NeonMagenta, NeonPurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = avatarTraits.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = NeonMagenta.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "GEMINI AI",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NeonMagenta,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${avatarTraits.personality} • ${avatarTraits.hairStyle} • ${avatarTraits.clothingStyle}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // TTS Auto-Play Voice Toggle Button
                        IconButton(
                            onClick = {
                                autoSpeakEnabled = !autoSpeakEnabled
                                if (!autoSpeakEnabled) stopSpeaking()
                                val statusText = if (autoSpeakEnabled) "Voice auto-play ON" else "Voice auto-play OFF"
                                Toast.makeText(context, statusText, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("btn_toggle_tts")
                        ) {
                            Icon(
                                imageVector = if (autoSpeakEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = "Toggle Audio TTS",
                                tint = if (autoSpeakEnabled) NeonCyan else TextMuted
                            )
                        }

                        // Stop playback button if actively speaking
                        if (isSpeaking) {
                            IconButton(
                                onClick = { stopSpeaking() },
                                modifier = Modifier.testTag("btn_stop_speech")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Stop Speech",
                                    tint = NeonMagenta
                                )
                            }
                        }

                        // Reset Chat Button
                        IconButton(
                            onClick = {
                                stopSpeaking()
                                messages.clear()
                                messages.add(
                                    AvatarChatMessage(
                                        senderName = avatarTraits.name,
                                        text = initialGreeting,
                                        isFromUser = false
                                    )
                                )
                                Toast.makeText(context, "Chat reset", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("btn_reset_avatar_chat")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear Chat",
                                tint = TextSecondary
                            )
                        }
                    }
                }
            }

            // Chat Messages List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(messages, key = { it.id }) { msg ->
                        val isUser = msg.isFromUser
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                color = if (isUser) NeonPurple.copy(alpha = 0.25f) else DarkSurfaceVariant,
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 16.dp
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isUser) NeonPurple else NeonMagenta.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .testTag(if (isUser) "user_chat_bubble" else "avatar_chat_bubble")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = msg.senderName,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isUser) NeonCyan else NeonMagenta,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )

                                        if (!isUser) {
                                            IconButton(
                                                onClick = { speakText(msg.text) },
                                                modifier = Modifier.size(22.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.VolumeUp,
                                                    contentDescription = "Read aloud",
                                                    tint = TextMuted,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = msg.text,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    )

                                    msg.actions?.generatePhoto?.let { photo ->
                                        if (photo.trigger && photo.prompt.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(
                                                color = DarkSurface,
                                                shape = RoundedCornerShape(8.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CameraAlt,
                                                        contentDescription = null,
                                                        tint = NeonCyan,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Column {
                                                        Text(
                                                            text = "PHOTO_GEN [${photo.style} ${photo.aspectRatio}]",
                                                            style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                                        )
                                                        Text(
                                                            text = photo.prompt,
                                                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    msg.actions?.generateVideo?.let { video ->
                                        if (video.trigger && video.prompt.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(
                                                color = DarkSurface,
                                                shape = RoundedCornerShape(8.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonMagenta.copy(alpha = 0.5f))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Videocam,
                                                        contentDescription = null,
                                                        tint = NeonMagenta,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Column {
                                                        Text(
                                                            text = "VIDEO_GEN [${video.cameraMovement} • ${video.durationSec}s]",
                                                            style = MaterialTheme.typography.labelSmall.copy(color = NeonMagenta, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                                        )
                                                        Text(
                                                            text = video.prompt,
                                                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    msg.actions?.avatarExpression?.let { expr ->
                                        if (expr.mood.isNotBlank() && expr.mood != "neutral") {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Psychology,
                                                    contentDescription = null,
                                                    tint = NeonPurple,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Mood: ${expr.mood} (${expr.animationTrigger})",
                                                    style = MaterialTheme.typography.labelSmall.copy(color = NeonPurple, fontSize = 10.sp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Animated Typing Indicator
                    if (isGenerating) {
                        item {
                            AnimatedTypingBubble(avatarName = avatarTraits.name)
                        }
                    }
                }
            }

            // Quick Prompt Suggestions
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickPrompts) { prompt ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { sendMessage(prompt) },
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            // Bottom Input Bar with Mic STT & Send
            Surface(
                color = DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isValid = inputText.trim().isNotBlank() && !isGenerating

                    // Speech To Text Microphone Button
                    IconButton(
                        onClick = { startVoiceInput() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isListening) NeonMagenta.copy(alpha = 0.3f) else DarkSurface)
                            .border(1.dp, if (isListening) NeonMagenta else DarkBorder, CircleShape)
                            .testTag("btn_mic_speech_to_text")
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                            contentDescription = "Voice Input (STT)",
                            tint = if (isListening) NeonMagenta else NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = if (isListening) "Listening..." else "Message ${avatarTraits.name}...",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_avatar_chat"),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (isValid) {
                                    sendMessage(inputText)
                                    inputText = ""
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonMagenta,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(18.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            if (isValid) {
                                sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                brush = if (isValid)
                                    Brush.linearGradient(listOf(NeonMagenta, NeonPurple))
                                else Brush.linearGradient(listOf(DarkSurface, DarkSurface)),
                                shape = CircleShape
                            )
                            .testTag("btn_send_avatar_chat")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (isValid) Color.White else TextMuted
                        )
                    }
                }
            }
        }
    }
}

/**
 * Animated typing indicator bubble displaying bouncing dot keyframes.
 */
@Composable
fun AnimatedTypingBubble(avatarName: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_dots_transition")

    val dot1Scale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dot2Scale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 180),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dot3Scale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 360),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = DarkSurfaceVariant,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonMagenta.copy(alpha = 0.4f)),
            modifier = Modifier.testTag("typing_indicator_bubble")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$avatarName is typing",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = NeonMagenta,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size((7 * dot1Scale).dp)
                            .clip(CircleShape)
                            .background(NeonMagenta)
                    )
                    Box(
                        modifier = Modifier
                            .size((7 * dot2Scale).dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                    )
                    Box(
                        modifier = Modifier
                            .size((7 * dot3Scale).dp)
                            .clip(CircleShape)
                            .background(NeonPurple)
                    )
                }
            }
        }
    }
}
