package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import com.example.data.model.ChatMessageEntity
import com.example.data.model.PersonaEntity
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Palette
import com.example.ui.components.AvatarSelectionBottomSheet
import com.example.ui.components.ChatMessageItem
import com.example.ui.components.ThemeSelectionBottomSheet
import com.example.ui.theme.ChatTheme
import com.example.ui.theme.ChatThemePresets
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RoleplayChatScreen(
    persona: PersonaEntity?,
    allPersonas: List<PersonaEntity> = emptyList(),
    messages: List<ChatMessageEntity>,
    isGenerating: Boolean,
    chatTheme: ChatTheme = ChatThemePresets.CyberpunkNeon,
    autoThemeMode: Boolean = true,
    onSendMessage: (String) -> Unit,
    onSelectPersona: (PersonaEntity) -> Unit = {},
    onCreateCustomModelRequested: () -> Unit = {},
    onDeletePersona: (String) -> Unit = {},
    onClearHistory: () -> Unit,
    onPlayAudio: (String) -> Unit,
    onSelectTheme: (ChatTheme) -> Unit = {},
    onToggleAutoTheme: (Boolean) -> Unit = {}
) {
    var inputText by remember { mutableStateOf("") }
    var showAvatarGallerySheet by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val quickActionPrompts = remember(persona) {
        listOf(
            "*smirks softly* What are you thinking right now?",
            "*steps closer into the light* Tell me a secret...",
            "*whispers softly* Let's change our location.",
            "*looks into your eyes* Describe your ideal scene with me."
        )
    }

    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            val targetIndex = if (isGenerating) messages.size else messages.size - 1
            if (targetIndex >= 0) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    if (showAvatarGallerySheet) {
        AvatarSelectionBottomSheet(
            personas = allPersonas.ifEmpty { if (persona != null) listOf(persona) else emptyList() },
            selectedPersonaId = persona?.id,
            onDismissRequest = { showAvatarGallerySheet = false },
            onSelectPersona = { newPersona ->
                onSelectPersona(newPersona)
                showAvatarGallerySheet = false
            },
            onCreateNewAvatar = {
                showAvatarGallerySheet = false
                onCreateCustomModelRequested()
            },
            onDeletePersona = onDeletePersona
        )
    }

    if (showThemeSheet) {
        ThemeSelectionBottomSheet(
            currentThemeId = chatTheme.id,
            autoThemeMode = autoThemeMode,
            onDismissRequest = { showThemeSheet = false },
            onSelectTheme = { theme ->
                onSelectTheme(theme)
                showThemeSheet = false
            },
            onToggleAutoTheme = onToggleAutoTheme
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(chatTheme.gradientBackground))
            .testTag("roleplay_chat_screen")
    ) {
        // Top App Bar
        Surface(
            color = chatTheme.topBarColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (persona != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showAvatarGallerySheet = true }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(chatTheme.accentColor, chatTheme.userBubbleBorderColor))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = persona.avatarSymbol, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = persona.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = chatTheme.accentColor.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = persona.avatarCategory,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = chatTheme.accentColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${persona.title} • ${chatTheme.name}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = chatTheme.actionTextColor,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Theme Selector Button
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showThemeSheet = true }
                                .testTag("btn_open_theme_selector"),
                            color = chatTheme.accentColor.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, chatTheme.accentColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Theme Selector",
                                    tint = chatTheme.accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Theme",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = chatTheme.accentColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Switch Avatar Button
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showAvatarGallerySheet = true }
                                .testTag("btn_switch_avatar_gallery"),
                            color = DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = "Avatar Gallery",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Avatars",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = onClearHistory,
                            modifier = Modifier.testTag("btn_clear_chat_history")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear History",
                                tint = TextSecondary
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "No Model Selected",
                            style = MaterialTheme.typography.titleMedium.copy(color = TextSecondary)
                        )

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showAvatarGallerySheet = true }
                                .testTag("btn_open_avatar_gallery_empty"),
                            color = NeonCyan.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Face, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Select Avatar", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        if (persona == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Please select an AI female model from the Studio tab to start roleplay.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            // Chat Message List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty()) {
                    // Empty state scenario card
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Brush.radialGradient(listOf(NeonMagenta, NeonPurple))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = persona.avatarSymbol, fontSize = 32.sp)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = persona.name,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = persona.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = NeonMagenta)
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Surface(
                                    color = DarkSurface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = NeonCyan,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "CURRENT SCENARIO",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = NeonCyan,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = persona.scenario,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextSecondary,
                                                fontSize = 12.sp,
                                                lineHeight = 16.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Send a message or tap a roleplay prompt below to begin continuous 1-on-1 dialogue.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            ChatMessageItem(
                                message = msg,
                                persona = persona,
                                chatTheme = chatTheme,
                                onPlayAudio = onPlayAudio
                            )
                        }

                        if (isGenerating) {
                            item {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = chatTheme.accentColor,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${persona.name} is typing roleplay action...",
                                        style = MaterialTheme.typography.labelSmall.copy(color = chatTheme.accentColor)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Action Prompt Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickActionPrompts) { prompt ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onSendMessage(prompt)
                            },
                        color = chatTheme.surfaceColor,
                        border = androidx.compose.foundation.BorderStroke(1.dp, chatTheme.accentColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Bottom Input Dock
            Surface(
                color = chatTheme.topBarColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isInputValid = inputText.trim().isNotBlank() && !isGenerating

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Type action e.g. *smirks* or roleplay prompt...", color = TextMuted, fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_roleplay_chat"),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (isInputValid) {
                                    onSendMessage(inputText.trim())
                                    inputText = ""
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = chatTheme.accentColor,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (isInputValid) {
                                onSendMessage(inputText.trim())
                                inputText = ""
                            }
                        },
                        enabled = isInputValid,
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                brush = if (isInputValid)
                                    Brush.linearGradient(listOf(chatTheme.accentColor, chatTheme.userBubbleBorderColor))
                                else Brush.linearGradient(listOf(DarkSurfaceVariant, DarkSurfaceVariant)),
                                shape = CircleShape
                            )
                            .testTag("btn_send_chat_message")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (isInputValid) Color.White else TextMuted
                        )
                    }
                }
            }
        }
    }
}
