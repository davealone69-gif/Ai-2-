package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ChatMessageEntity
import com.example.data.model.PersonaEntity
import com.example.ui.theme.ChatTheme
import com.example.ui.theme.ChatThemePresets
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatMessageItem(
    message: ChatMessageEntity,
    persona: PersonaEntity?,
    chatTheme: ChatTheme = ChatThemePresets.CyberpunkNeon,
    onPlayAudio: (String) -> Unit
) {
    val isUser = message.sender == "user"
    val timeStr = remember(message.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }
    var showInspector by remember { mutableStateOf(false) }
    var isVideoPlaying by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("chat_message_item_${message.id}"),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // Model Avatar symbol / image
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(
                                chatTheme.accentColor,
                                chatTheme.surfaceColor
                            )
                        )
                    )
                    .border(1.dp, chatTheme.accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!persona?.avatarImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = persona?.avatarImageUrl,
                        contentDescription = persona?.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = persona?.avatarSymbol ?: "💋",
                        fontSize = 18.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            // 1. CHAT TEXT BUBBLE (Pass payloads.chat.text directly to chat interface)
            Surface(
                color = if (isUser) chatTheme.userBubbleColor else chatTheme.aiBubbleColor,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isUser) chatTheme.userBubbleBorderColor else chatTheme.aiBubbleBorderColor
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (!isUser) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = persona?.name ?: "AI Model",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = chatTheme.accentColor
                                )
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen voice",
                                tint = chatTheme.actionTextColor,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onPlayAudio(message.text) }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Format message text with highlighted action text *action*
                    val annotatedString = remember(message.text, chatTheme) {
                        buildAnnotatedString {
                            val regex = Regex("\\*(.*?)\\*")
                            var lastIndex = 0
                            regex.findAll(message.text).forEach { matchResult ->
                                val range = matchResult.range
                                append(message.text.substring(lastIndex, range.first))
                                withStyle(
                                    style = SpanStyle(
                                        color = chatTheme.actionTextColor,
                                        fontStyle = FontStyle.Italic,
                                        fontWeight = FontWeight.Medium
                                    )
                                ) {
                                    append("*${matchResult.groupValues[1]}*")
                                }
                                lastIndex = range.last + 1
                            }
                            if (lastIndex < message.text.length) {
                                append(message.text.substring(lastIndex))
                            }
                        }
                    }

                    Text(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isUser) chatTheme.userBubbleTextColor else chatTheme.aiBubbleTextColor,
                            lineHeight = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            // 2. PHOTO GENERATION PIPELINE OUTPUT (Direct payloads.photo_generation.prompt to Image Pipeline)
            if (!isUser && (!message.photoPrompt.isNullOrBlank() || !message.photoUrl.isNullOrBlank())) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonRose.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .testTag("photo_pipeline_card_${message.id}")
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Photo Pipeline",
                                    tint = NeonRose,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PHOTO GENERATION PIPELINE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonRose,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Surface(
                                color = NeonRose.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = message.photoAspectRatio ?: "1:1",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonRose,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (!message.photoPrompt.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Prompt: ${message.photoPrompt}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontStyle = FontStyle.Italic
                                )
                            )
                        }

                        if (!message.photoUrl.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(
                                        when (message.photoAspectRatio) {
                                            "9:16" -> 0.75f
                                            "16:9" -> 1.77f
                                            else -> 1f
                                        }
                                    )
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkObsidian)
                            ) {
                                AsyncImage(
                                    model = message.photoUrl,
                                    contentDescription = "Generated Photo Output",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }

            // 3. VIDEO MODEL PIPELINE OUTPUT (Direct payloads.video_generation.prompt to Video Pipeline)
            if (!isUser && !message.videoPrompt.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .testTag("video_pipeline_card_${message.id}")
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Video Pipeline",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "VIDEO MODEL PIPELINE (RUNWAY / LUMA)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Row {
                                Surface(
                                    color = NeonCyan.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = message.videoCameraMotion ?: "pan",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NeonCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    color = NeonMagenta.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${message.videoDurationSec ?: 5}s",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NeonMagenta,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Prompt: ${message.videoPrompt}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontStyle = FontStyle.Italic
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkObsidian)
                                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .clickable { isVideoPlaying = !isVideoPlaying },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!persona?.avatarImageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = persona?.avatarImageUrl,
                                    contentDescription = "Video Frame Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = if (isVideoPlaying) 0.1f else 0.4f))
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(DarkSurface.copy(alpha = 0.85f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play Video",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isVideoPlaying) "Rendering Clip..." else "Play Video Scene",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 4. AVATAR STATE ENGINE PARAMETERS (Send payloads.avatar_state parameters to Live2D / Avatar Engine)
            if (!isUser && (!message.avatarExpression.isNullOrBlank() || !message.avatarGesture.isNullOrBlank())) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Live2D Avatar Engine",
                        tint = NeonGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Live2D Avatar: ${message.avatarExpression ?: "neutral"} | Gesture: ${message.avatarGesture ?: "idle"}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // 5. SWARM AUDIT INSPECTOR TOGGLE
            if (!isUser && (!message.rawPayloadJson.isNullOrBlank() || !message.memoryUpdatesJson.isNullOrBlank())) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurface)
                        .clickable { showInspector = !showInspector }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = "SWARM Inspector",
                        tint = NeonGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showInspector) "Hide SWARM Audit" else "Inspect SWARM Master Payload",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonGreen,
                            fontSize = 10.sp
                        )
                    )
                }

                AnimatedVisibility(visible = showInspector, enter = fadeIn()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .padding(top = 6.dp),
                        color = DarkSurface,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SWARM_MASTER SYSTEM PAYLOAD LOG",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            if (!message.memoryUpdatesJson.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Memory Updates: ${message.memoryUpdatesJson}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = NeonGold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            if (!message.rawPayloadJson.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = message.rawPayloadJson,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
