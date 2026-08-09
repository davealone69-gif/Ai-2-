package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppPreferences
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.SettingsViewModel
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    appPreferences: AppPreferences,
    settingsViewModel: SettingsViewModel,
    onSpeakTestText: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showResetDialog by remember { mutableStateOf(false) }

    // Local editable fields for user profile
    var usernameText by remember(appPreferences.username) { mutableStateOf(appPreferences.username) }
    var emailText by remember(appPreferences.email) { mutableStateOf(appPreferences.email) }

    // Temperature slider state
    var tempSliderValue by remember(appPreferences.aiTemperature) { mutableFloatStateOf(appPreferences.aiTemperature) }

    // TopP slider state
    var topPSliderValue by remember(appPreferences.topP) { mutableFloatStateOf(appPreferences.topP) }

    // Max tokens slider state
    var maxTokensValue by remember(appPreferences.maxOutputTokens) { mutableFloatStateOf(appPreferences.maxOutputTokens.toFloat()) }

    val availableModels = listOf(
        "gemini-1.5-flash",
        "gemini-1.5-pro",
        "gemini-2.0-flash",
        "gemini-exp"
    )

    val themePresets = listOf(
        "Dark Obsidian",
        "Neon Cyberpunk",
        "Midnight Violet",
        "Sunset Gold",
        "Light Glass"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkObsidian)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(scrollState)
            .testTag("settings_screen_container")
    ) {
        // Screen Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Preferences",
                        tint = NeonMagenta,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "App Preferences",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                }
                Text(
                    text = "Configured & persisted real-time via DataStore API",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
            }

            // Persistence Status Indicator Badge
            Surface(
                color = NeonCyan.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = "DataStore",
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "DataStore Active",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Section 1: AI Model Configuration
        SettingsCategoryCard(
            title = "AI Model Engine Configuration",
            icon = Icons.Default.Psychology,
            accentColor = NeonMagenta
        ) {
            // Temperature Control
            Text(
                text = "Model Temperature (Creativity)",
                style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "Lower values produce precise/deterministic outputs; higher values increase randomness and creative flair.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val tempLabel = when {
                    tempSliderValue < 0.3f -> "Precise & Focused (${String.format(Locale.US, "%.2f", tempSliderValue)})"
                    tempSliderValue < 0.8f -> "Balanced Creativity (${String.format(Locale.US, "%.2f", tempSliderValue)})"
                    else -> "Highly Expressive (${String.format(Locale.US, "%.2f", tempSliderValue)})"
                }
                Text(
                    text = tempLabel,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = NeonMagenta,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Slider(
                value = tempSliderValue,
                onValueChange = { newValue ->
                    tempSliderValue = newValue
                },
                onValueChangeFinished = {
                    settingsViewModel.updateAiTemperature(tempSliderValue)
                    Toast.makeText(context, "AI Temperature saved: ${String.format(Locale.US, "%.2f", tempSliderValue)}", Toast.LENGTH_SHORT).show()
                },
                valueRange = 0.0f..1.5f,
                steps = 29,
                colors = SliderDefaults.colors(
                    thumbColor = NeonMagenta,
                    activeTrackColor = NeonMagenta,
                    inactiveTrackColor = DarkSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_temp_slider")
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = DarkBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // AI Model Selection
            Text(
                text = "Default Gemini Model",
                style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "Select default foundational LLM for text generation and roleplay chat.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                availableModels.forEach { model ->
                    val isSelected = appPreferences.aiModelName == model
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            settingsViewModel.updateAiModelName(model)
                            Toast.makeText(context, "Default model set to $model", Toast.LENGTH_SHORT).show()
                        },
                        label = {
                            Text(
                                text = model,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            )
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonMagenta,
                            containerColor = DarkSurfaceVariant
                        ),
                        modifier = Modifier.testTag("model_select_chip_${model.replace(".", "_")}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = DarkBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // TopP Control
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top-P Nucleus Sampling: ${String.format(Locale.US, "%.2f", topPSliderValue)}",
                    style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                )
            }
            Slider(
                value = topPSliderValue,
                onValueChange = { topPSliderValue = it },
                onValueChangeFinished = {
                    settingsViewModel.updateTopP(topPSliderValue)
                },
                valueRange = 0.1f..1.0f,
                steps = 18,
                colors = SliderDefaults.colors(
                    thumbColor = NeonCyan,
                    activeTrackColor = NeonCyan,
                    inactiveTrackColor = DarkSurfaceVariant
                ),
                modifier = Modifier.testTag("top_p_slider")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Max Output Tokens Control
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Max Output Tokens: ${maxTokensValue.toInt()}",
                    style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                )
            }
            Slider(
                value = maxTokensValue,
                onValueChange = { maxTokensValue = it },
                onValueChangeFinished = {
                    settingsViewModel.updateMaxOutputTokens(maxTokensValue.toInt())
                },
                valueRange = 256f..8192f,
                steps = 30,
                colors = SliderDefaults.colors(
                    thumbColor = NeonPurple,
                    activeTrackColor = NeonPurple,
                    inactiveTrackColor = DarkSurfaceVariant
                ),
                modifier = Modifier.testTag("max_tokens_slider")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 2: Visual Theme & Appearance
        SettingsCategoryCard(
            title = "Theme & Visual Appearance",
            icon = Icons.Default.Palette,
            accentColor = NeonCyan
        ) {
            // Theme Preset Selection
            Text(
                text = "Application Theme Palette",
                style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "Choose your preferred canvas aesthetic and UI color tokens.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                themePresets.forEach { theme ->
                    val isSelected = appPreferences.themePreference == theme
                    val chipBorderColor = if (isSelected) NeonCyan else DarkBorder

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else DarkSurfaceVariant)
                            .border(1.dp, chipBorderColor, RoundedCornerShape(12.dp))
                            .clickable {
                                settingsViewModel.updateThemePreference(theme)
                                Toast.makeText(context, "Theme set to $theme", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("theme_chip_${theme.lowercase().replace(" ", "_")}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = theme,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isSelected) NeonCyan else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = DarkBorder, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Dark Mode Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (appPreferences.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Dark Mode",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Dark Obsidian Canvas",
                            style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Text(
                        text = "High-contrast dark obsidian background optimized for low-light editing.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }

                Switch(
                    checked = appPreferences.isDarkMode,
                    onCheckedChange = { isChecked ->
                        settingsViewModel.updateDarkMode(isChecked)
                        Toast.makeText(context, "Dark mode: $isChecked", Toast.LENGTH_SHORT).show()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = NeonCyan,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DarkSurfaceVariant
                    ),
                    modifier = Modifier.testTag("dark_mode_switch")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Auto Dynamic Theme Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Auto Theme",
                            tint = NeonMagenta,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Character Dynamic Auto-Theme",
                            style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Text(
                        text = "Automatically adjust chat color palette based on active roleplay character avatar.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }

                Switch(
                    checked = appPreferences.autoThemeEnabled,
                    onCheckedChange = { isChecked ->
                        settingsViewModel.updateAutoThemeEnabled(isChecked)
                        Toast.makeText(context, "Auto-theme: $isChecked", Toast.LENGTH_SHORT).show()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = NeonMagenta,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DarkSurfaceVariant
                    ),
                    modifier = Modifier.testTag("auto_theme_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 3: Audio & TTS Settings
        SettingsCategoryCard(
            title = "Audio & TTS Voice Narration",
            icon = Icons.Default.VolumeUp,
            accentColor = NeonPurple
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TTS Auto-Speech Narration",
                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Automatically pronounce generated responses using Android Text-to-Speech.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }

                Switch(
                    checked = appPreferences.ttsAutoPlay,
                    onCheckedChange = { isChecked ->
                        settingsViewModel.updateTtsAutoPlay(isChecked)
                        Toast.makeText(context, "TTS auto-play: $isChecked", Toast.LENGTH_SHORT).show()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = NeonPurple,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DarkSurfaceVariant
                    ),
                    modifier = Modifier.testTag("tts_autoplay_switch")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    onSpeakTestText("DataStore preferences loaded successfully. Android speech synthesis operational.")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPurple.copy(alpha = 0.2f),
                    contentColor = NeonPurple
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("test_voice_button")
            ) {
                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Test Speech", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Test Audio Narration Speech", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section 4: Account & User Profile
        SettingsCategoryCard(
            title = "User Profile & Data Preferences",
            icon = Icons.Default.Person,
            accentColor = Color(0xFFF59E0B)
        ) {
            OutlinedTextField(
                value = usernameText,
                onValueChange = { usernameText = it },
                label = { Text("Display Name") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF59E0B),
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = Color(0xFFF59E0B),
                    unfocusedLabelColor = TextMuted
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_username_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = emailText,
                onValueChange = { emailText = it },
                label = { Text("Email Address") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF59E0B),
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = Color(0xFFF59E0B),
                    unfocusedLabelColor = TextMuted
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_email_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "In-App Notifications",
                            style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Text(
                        text = "Receive notifications when long video generations complete.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }

                Switch(
                    checked = appPreferences.notificationsEnabled,
                    onCheckedChange = { isChecked ->
                        settingsViewModel.updateNotificationsEnabled(isChecked)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFF59E0B),
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DarkSurfaceVariant
                    ),
                    modifier = Modifier.testTag("notifications_switch")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        settingsViewModel.updateUserProfile(usernameText, emailText)
                        Toast.makeText(context, "User profile saved to DataStore!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF59E0B),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_profile_button")
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Profile", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceVariant,
                        contentColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("reset_defaults_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Reset Preferences?",
                    style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "This will clear all custom settings in DataStore and restore factory default parameters.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        settingsViewModel.resetToDefaults()
                        showResetDialog = false
                        Toast.makeText(context, "Preferences restored to defaults", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Reset All", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun SettingsCategoryCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }

            content()
        }
    }
}
