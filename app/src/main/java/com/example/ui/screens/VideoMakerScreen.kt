package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.PersonaEntity
import com.example.data.model.VideoProjectEntity
import com.example.data.model.VideoSceneItem
import com.example.ui.components.VideoSceneCard
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoMakerScreen(
    personas: List<PersonaEntity>,
    selectedPersona: PersonaEntity?,
    videoProjects: List<VideoProjectEntity>,
    activeProject: VideoProjectEntity?,
    scenes: List<VideoSceneItem>,
    isGenerating: Boolean,
    isAudioPlaying: Boolean,
    onGenerateVideo: (title: String, personaId: String, prompt: String, style: String, sceneCount: Int) -> Unit,
    onSelectProject: (VideoProjectEntity) -> Unit,
    onToggleAudio: () -> Unit,
    onPlaySceneAudio: (String) -> Unit
) {
    var titleInput by remember { mutableStateOf("") }
    var promptInput by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Photorealistic") }
    var sceneCount by remember { mutableFloatStateOf(4f) }
    var selectedPersonaId by remember { mutableStateOf(selectedPersona?.id ?: (personas.firstOrNull()?.id ?: "")) }
    var activeSceneIndex by remember { mutableIntStateOf(0) }

    val styles = listOf("Photorealistic", "Cyberpunk", "Cinematic Noir", "Anime 3D", "Fantasy")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkObsidian)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("video_maker_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFF2E083B),
                                Color(0xFF0F1B38)
                            )
                        )
                    )
                    .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(NeonMagenta, NeonCyan))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "AURA AI VIDEO MAKER",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Multi-Scene Storyboard & Narration Engine",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }
                }
            }
        }

        // Script & Storyboard Creation Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CREATE NEW VIDEO STORYBOARD",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonMagenta,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Model Selector
                    Text(
                        text = "Starring AI Model",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(personas) { p ->
                            val isSelected = p.id == selectedPersonaId
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedPersonaId = p.id },
                                color = if (isSelected) NeonMagenta.copy(alpha = 0.25f) else DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) NeonMagenta else DarkBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = p.avatarSymbol, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = p.name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) NeonMagenta else TextSecondary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }
                    }

                    if (videoProjects.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Saved Storyboard Projects",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(videoProjects) { proj ->
                                val isSelected = proj.id == activeProject?.id
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onSelectProject(proj) },
                                    color = if (isSelected) NeonCyan.copy(alpha = 0.25f) else DarkSurface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) NeonCyan else DarkBorder
                                    )
                                ) {
                                    Text(
                                        text = proj.title,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) NeonCyan else TextSecondary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val isTitleValid = titleInput.trim().isNotBlank()
                    val isPromptValid = promptInput.trim().isNotBlank()
                    val isVideoFormValid = isTitleValid && isPromptValid && !isGenerating

                    // Title Input
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Video Title (e.g. Neo-Tokyo Rain)", color = TextMuted) },
                        supportingText = {
                            if (!isTitleValid && titleInput.isNotEmpty()) {
                                Text("Title cannot be empty spaces", color = NeonMagenta, fontSize = 11.sp)
                            } else if (!isTitleValid) {
                                Text("Required *", color = TextMuted, fontSize = 11.sp)
                            } else {
                                Text("✓ Valid title", color = NeonCyan, fontSize = 11.sp)
                            }
                        },
                        isError = titleInput.isNotEmpty() && !isTitleValid,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonMagenta,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Prompt Input
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        label = { Text("Video Story Concept / Narrative Prompt", color = TextMuted) },
                        supportingText = {
                            if (!isPromptValid && promptInput.isNotEmpty()) {
                                Text("Concept prompt cannot be empty spaces", color = NeonMagenta, fontSize = 11.sp)
                            } else if (!isPromptValid) {
                                Text("Required *", color = TextMuted, fontSize = 11.sp)
                            } else {
                                Text("✓ Valid concept prompt", color = NeonCyan, fontSize = 11.sp)
                            }
                        },
                        isError = promptInput.isNotEmpty() && !isPromptValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_video_prompt"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonMagenta,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Style Selector
                    Text(
                        text = "Visual Render Style",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(styles) { st ->
                            val isSelected = st == selectedStyle
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedStyle = st },
                                color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) NeonCyan else DarkBorder
                                )
                            ) {
                                Text(
                                    text = st,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) NeonCyan else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Scene count slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Storyboard Scene Count",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${sceneCount.toInt()} Scenes",
                            style = MaterialTheme.typography.labelSmall.copy(color = NeonMagenta, fontWeight = FontWeight.Bold)
                        )
                    }
                    Slider(
                        value = sceneCount,
                        onValueChange = { sceneCount = it },
                        valueRange = 3f..6f,
                        steps = 2,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonMagenta,
                            activeTrackColor = NeonPurple,
                            inactiveTrackColor = DarkBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (isVideoFormValid) {
                                val targetPersonaId = selectedPersonaId.ifEmpty { personas.firstOrNull()?.id ?: "preset_valeria" }
                                onGenerateVideo(titleInput.trim(), targetPersonaId, promptInput.trim(), selectedStyle, sceneCount.toInt())
                            }
                        },
                        enabled = isVideoFormValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_generate_video_script"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Unspecified),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(
                                    brush = if (isVideoFormValid) {
                                        Brush.horizontalGradient(listOf(NeonMagenta, NeonPurple, NeonCyan))
                                    } else {
                                        Brush.horizontalGradient(listOf(DarkSurfaceVariant, DarkSurfaceVariant))
                                    },
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isGenerating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generating Script & Storyboard...", color = Color.White)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = if (isVideoFormValid) Color.White else TextMuted
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when {
                                            !isTitleValid -> "Enter Video Title to Continue"
                                            !isPromptValid -> "Enter Story Concept to Continue"
                                            else -> "Generate AI Video Storyboard"
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isVideoFormValid) Color.White else TextMuted
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Video Player Simulator section
        if (scenes.isNotEmpty()) {
            item {
                Text(
                    text = "STORYBOARD VIDEO PLAYER & PREVIEW",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonCyan,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                val safeSceneIndex = activeSceneIndex.coerceIn(0, scenes.size - 1)
                val currentScene = scenes[safeSceneIndex]

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Player Screen Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF230F3B),
                                            Color(0xFF0C192E)
                                        )
                                    )
                                )
                                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Surface(
                                    color = NeonMagenta.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "SCENE ${currentScene.sceneNumber} / ${scenes.size} • ${currentScene.shotType}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Icon(
                                    imageVector = Icons.Default.CameraRoll,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(36.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = currentScene.visualPrompt,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 2
                                )
                            }

                            // Camera Motion Indicator Overlay
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp),
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currentScene.cameraMotion,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NeonCyan,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Player Control Dock
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onToggleAudio,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(NeonMagenta, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = if (isAudioPlaying) "Playing Narration Audio..." else "Playback Paused",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isAudioPlaying) NeonMagenta else TextSecondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Audio: ${currentScene.audioMood}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            // Scene Next/Prev Quick Selector
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        if (safeSceneIndex > 0) activeSceneIndex = safeSceneIndex - 1
                                    },
                                    enabled = safeSceneIndex > 0,
                                    modifier = Modifier.size(32.dp).testTag("btn_prev_scene")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                                        contentDescription = "Previous Scene",
                                        tint = if (safeSceneIndex > 0) NeonCyan else TextMuted
                                    )
                                }

                                scenes.forEachIndexed { index, sc ->
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (index == safeSceneIndex) NeonCyan else DarkSurface
                                            )
                                            .clickable { activeSceneIndex = index },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${sc.sceneNumber}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (index == safeSceneIndex) Color.Black else TextSecondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        if (safeSceneIndex < scenes.size - 1) activeSceneIndex = safeSceneIndex + 1
                                    },
                                    enabled = safeSceneIndex < scenes.size - 1,
                                    modifier = Modifier.size(32.dp).testTag("btn_next_scene")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                                        contentDescription = "Next Scene",
                                        tint = if (safeSceneIndex < scenes.size - 1) NeonCyan else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Scene Cards Breakdown List
            items(scenes, key = { it.sceneNumber }) { sc ->
                VideoSceneCard(
                    scene = sc,
                    onPlayAudio = { onPlaySceneAudio(sc.narrationText) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}
