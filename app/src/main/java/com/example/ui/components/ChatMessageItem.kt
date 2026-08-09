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
import androidx.compose.material.icons.filled.CameraAlt
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
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
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
    var showSnapshotCard by remember { mutableStateOf(false) }

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
                    .size(36.dp)
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
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
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

            // Quick Snapshot Generation Trigger for AI model responses
            if (!isUser && persona != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurface)
                        .clickable { showSnapshotCard = !showSnapshotCard }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Visual Snapshot",
                        tint = NeonRose,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showSnapshotCard) "Hide Snapshot" else "Render Visual Snapshot",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonRose,
                            fontSize = 10.sp
                        )
                    )
                }

                AnimatedVisibility(visible = showSnapshotCard, enter = fadeIn()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(top = 6.dp),
                        color = DarkSurface,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonRose.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI Keyframe Portrait Prompt",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "High Resolution 8K Render of ${persona.name} (${persona.avatarCategory}): ${message.actionText ?: message.text.take(60)}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
