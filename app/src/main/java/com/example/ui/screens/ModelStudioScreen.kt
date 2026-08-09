package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PersonaEntity
import com.example.ui.components.AvatarSelectionBottomSheet
import com.example.ui.components.CustomModelBottomSheet
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
fun ModelStudioScreen(
    personas: List<PersonaEntity>,
    onSelectPersonaForChat: (PersonaEntity) -> Unit,
    onSelectPersonaForVideo: (PersonaEntity) -> Unit,
    onGenerateAvatarImage: (suspend (
        name: String,
        age: Int,
        title: String,
        category: String,
        personality: String,
        backstory: String
    ) -> Pair<String, String>)? = null,
    onCreateCustomModel: (
        name: String,
        title: String,
        age: Int,
        category: String,
        personality: String,
        backstory: String,
        scenario: String,
        voiceStyle: String,
        systemPrompt: String,
        creativity: Float,
        avatarImageUrl: String,
        avatarPrompt: String
    ) -> Unit,
    onDeleteCustomModel: (String) -> Unit
) {
    var showCreateSheet by remember { mutableStateOf(false) }
    var showFullCreateScreen by remember { mutableStateOf(false) }
    var showAvatarGallerySheet by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    if (showFullCreateScreen) {
        CreatePersonaScreen(
            onBack = { showFullCreateScreen = false },
            onGenerateAvatarImage = onGenerateAvatarImage,
            onSavePersona = { name, title, age, category, personality, backstory, scenario, voiceStyle, systemPrompt, creativity, avatarImageUrl, avatarPrompt ->
                onCreateCustomModel(
                    name, title, age, category, personality, backstory, scenario, voiceStyle, systemPrompt, creativity, avatarImageUrl, avatarPrompt
                )
                showFullCreateScreen = false
            }
        )
        return
    }

    val categories = listOf("All", "Photorealistic", "Cyberpunk", "Anime 3D", "Noir", "Fantasy")
    val filteredPersonas = remember(personas, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") personas
        else personas.filter { it.avatarCategory == selectedCategoryFilter }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkObsidian)
            .testTag("model_studio_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFF1B0B2A),
                                DarkObsidian
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AURA MODEL STUDIO",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary,
                                        fontSize = 20.sp,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NeonMagenta,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "Create & Roleplay with Custom AI Models",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }

                        Surface(
                            color = NeonMagenta.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonMagenta),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { showAvatarGallerySheet = true }
                                .testTag("btn_open_gallery_sheet")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = NeonMagenta,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${personas.size} Models Grid",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonMagenta,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Full Character Creator Action Banner
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { showFullCreateScreen = true }
                            .testTag("btn_open_full_creator_screen"),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple)
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
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(NeonMagenta, NeonPurple))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "+ Create Custom Character Persona",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Define Name, Age, Personality, Backstory & Save to Room DB",
                                        style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan, fontSize = 10.sp)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonMagenta,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Filters Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = cat == selectedCategoryFilter
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedCategoryFilter = cat },
                                color = if (isSelected) NeonMagenta.copy(alpha = 0.25f) else DarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) NeonMagenta else DarkBorder
                                )
                            ) {
                                Text(
                                    text = cat,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) NeonMagenta else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Model List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredPersonas, key = { it.id }) { persona ->
                    ModelCardItem(
                        persona = persona,
                        onChatClick = { onSelectPersonaForChat(persona) },
                        onVideoClick = { onSelectPersonaForVideo(persona) },
                        onDeleteClick = { onDeleteCustomModel(persona.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Floating Action Button to Add Model
        FloatingActionButton(
            onClick = { showCreateSheet = true },
            containerColor = NeonMagenta,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 76.dp, end = 20.dp)
                .testTag("fab_create_custom_model")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Model")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "New Model",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }

    if (showCreateSheet) {
        CustomModelBottomSheet(
            onDismiss = { showCreateSheet = false },
            onCreateModel = onCreateCustomModel
        )
    }

    if (showAvatarGallerySheet) {
        AvatarSelectionBottomSheet(
            personas = personas,
            selectedPersonaId = null,
            onDismissRequest = { showAvatarGallerySheet = false },
            onSelectPersona = { selected ->
                onSelectPersonaForChat(selected)
                showAvatarGallerySheet = false
            },
            onCreateNewAvatar = {
                showAvatarGallerySheet = false
                showCreateSheet = true
            },
            onDeletePersona = onDeleteCustomModel
        )
    }
}

@Composable
fun ModelCardItem(
    persona: PersonaEntity,
    onChatClick: () -> Unit,
    onVideoClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val cardGradient = remember(persona.avatarCategory) {
        when (persona.avatarCategory) {
            "Photorealistic" -> listOf(Color(0xFF28112B), DarkSurfaceVariant)
            "Cyberpunk" -> listOf(Color(0xFF0C2434), DarkSurfaceVariant)
            "Anime 3D" -> listOf(Color(0xFF331422), DarkSurfaceVariant)
            "Noir" -> listOf(Color(0xFF241530), DarkSurfaceVariant)
            else -> listOf(Color(0xFF2B1F0E), DarkSurfaceVariant)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("model_card_${persona.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(cardGradient))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar Symbol / Picture Box
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        NeonMagenta,
                                        NeonPurple
                                    )
                                )
                            )
                            .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (persona.avatarImageUrl.isNotBlank()) {
                            AsyncImage(
                                model = persona.avatarImageUrl,
                                contentDescription = persona.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = persona.avatarSymbol,
                                fontSize = 26.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = persona.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 18.sp
                                )
                            )
                            if (persona.isCustom) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = NeonCyan.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "CUSTOM",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NeonCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = persona.title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeonMagenta,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Style: ${persona.avatarCategory} • Voice: ${persona.voiceStyle}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                if (persona.isCustom) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Model",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scenario Description
            Surface(
                color = DarkSurface.copy(alpha = 0.7f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "ROLEPLAY SCENARIO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = persona.scenario,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (1-on-1 Roleplay & Video Maker)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onChatClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_chat_${persona.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "1-on-1 Roleplay",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                OutlinedButton(
                    onClick = onVideoClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_video_${persona.id}"),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPurple)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = NeonPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Video Content",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonPurple,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
