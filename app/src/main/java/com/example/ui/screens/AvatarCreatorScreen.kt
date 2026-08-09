package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AvatarTraits
import com.example.ui.components.ChatInterface
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
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AvatarCreatorScreen(
    persistedTraits: AvatarTraits,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Local Compose State initialized from DataStore traits
    var localTraits by remember(persistedTraits) { mutableStateOf(persistedTraits) }
    var selectedCategoryTab by remember { mutableIntStateOf(0) }
    var isSavedNotificationVisible by remember { mutableStateOf(false) }
    var showSaveConfirmationDialog by remember { mutableStateOf(false) }
    var showFullScreenPreview by remember { mutableStateOf(false) }
    var mainScreenTab by remember { mutableIntStateOf(0) } // 0 = Customizer, 1 = Gemini Chat

    val traitCategories = listOf("Hair", "Eyes", "Clothing", "Skin & Face", "Extras & Presets")

    // Option lists
    val hairStyles = listOf("Long Waves", "Bob Cut", "Anime Twin Tails", "Pixie Cut", "Braids", "Ponytail", "Afro Buns")
    val hairColors = mapOf(
        "Neon Pink" to Color(0xFFEC4899),
        "Raven Black" to Color(0xFF18181B),
        "Platinum Blonde" to Color(0xFFFEF08A),
        "Auburn Brown" to Color(0xFF9A3412),
        "Electric Cyan" to Color(0xFF06B6D4),
        "Lavender Purple" to Color(0xFFA855F7)
    )

    val eyeStyles = listOf("Anime Sparkle", "Cat Eye", "Almond Deep", "Rounded Glow", "Cybernetic Ring")
    val eyeColors = mapOf(
        "Sapphire Blue" to Color(0xFF2563EB),
        "Emerald Green" to Color(0xFF10B981),
        "Violet Flame" to Color(0xFF8B5CF6),
        "Amber Gold" to Color(0xFFF59E0B),
        "Ruby Red" to Color(0xFFEF4444),
        "Obsidian Black" to Color(0xFF1F2937)
    )

    val clothingStyles = listOf("Cyber Jacket", "Elegant Kimono", "Casual Hoodie", "Gothic Dress", "Futuristic Armor", "Academic Blazer")
    val clothingColors = mapOf(
        "Royal Purple" to Color(0xFF7C3AED),
        "Crimson Red" to Color(0xFFDC2626),
        "Midnight Black" to Color(0xFF09090B),
        "Neon Cyan" to Color(0xFF06B6D4),
        "Pastel White" to Color(0xFFF8FAFC)
    )

    val skinTones = mapOf(
        "Porcelain" to Color(0xFFFFF0E5),
        "Fair Pink" to Color(0xFFFFE4E1),
        "Golden Glow" to Color(0xFFE8B273),
        "Warm Bronze" to Color(0xFFD2895A),
        "Deep Cocoa" to Color(0xFF5C3317)
    )

    val expressions = listOf("Confident Smile", "Playful Wink", "Serene", "Thoughtful", "Fierce")
    val accessories = listOf("None", "Cyber Visor", "Cat Ear Headset", "Gold Earrings", "Star Hairpin", "Round Glasses")
    val backgroundStyles = listOf("Neon Grid", "Cyber City Night", "Sunset Glow", "Abstract Void", "Pastel Aura")
    val personalities = listOf("Cheerful", "Stoic", "Witty", "Tsundere", "Philosophical", "Sarcastic", "Nurturing")

    val stylePresets = listOf(
        "Cyberpunk" to AvatarTraits(
            name = "Vesper Cyberpunk",
            hairStyle = "Anime Twin Tails",
            hairColor = "Electric Cyan",
            eyeStyle = "Cybernetic Ring",
            eyeColor = "Sapphire Blue",
            clothingStyle = "Cyber Jacket",
            clothingColor = "Neon Cyan",
            skinTone = "Porcelain",
            expression = "Confident Smile",
            accessory = "Cyber Visor",
            backgroundStyle = "Neon Grid",
            personality = "Witty"
        ),
        "Fantasy" to AvatarTraits(
            name = "Lyra Mage",
            hairStyle = "Long Waves",
            hairColor = "Lavender Purple",
            eyeStyle = "Anime Sparkle",
            eyeColor = "Violet Flame",
            clothingStyle = "Elegant Kimono",
            clothingColor = "Royal Purple",
            skinTone = "Fair Pink",
            expression = "Serene",
            accessory = "Gold Earrings",
            backgroundStyle = "Sunset Glow",
            personality = "Philosophical"
        ),
        "Professional" to AvatarTraits(
            name = "Elena Executive",
            hairStyle = "Bob Cut",
            hairColor = "Raven Black",
            eyeStyle = "Almond Deep",
            eyeColor = "Obsidian Black",
            clothingStyle = "Academic Blazer",
            clothingColor = "Midnight Black",
            skinTone = "Golden Glow",
            expression = "Thoughtful",
            accessory = "Round Glasses",
            backgroundStyle = "Abstract Void",
            personality = "Stoic"
        ),
        "Anime Popstar" to AvatarTraits(
            name = "Kira Idol",
            hairStyle = "Ponytail",
            hairColor = "Neon Pink",
            eyeStyle = "Anime Sparkle",
            eyeColor = "Ruby Red",
            clothingStyle = "Gothic Dress",
            clothingColor = "Crimson Red",
            skinTone = "Porcelain",
            expression = "Playful Wink",
            accessory = "Cat Ear Headset",
            backgroundStyle = "Pastel Aura",
            personality = "Cheerful"
        ),
        "Sci-Fi Android" to AvatarTraits(
            name = "Aria Android",
            hairStyle = "Pixie Cut",
            hairColor = "Platinum Blonde",
            eyeStyle = "Cybernetic Ring",
            eyeColor = "Emerald Green",
            clothingStyle = "Futuristic Armor",
            clothingColor = "Pastel White",
            skinTone = "Porcelain",
            expression = "Fierce",
            accessory = "Star Hairpin",
            backgroundStyle = "Cyber City Night",
            personality = "Stoic"
        ),
        "Casual Chic" to AvatarTraits(
            name = "Maya Casual",
            hairStyle = "Braids",
            hairColor = "Auburn Brown",
            eyeStyle = "Rounded Glow",
            eyeColor = "Amber Gold",
            clothingStyle = "Casual Hoodie",
            clothingColor = "Royal Purple",
            skinTone = "Warm Bronze",
            expression = "Confident Smile",
            accessory = "None",
            backgroundStyle = "Pastel Aura",
            personality = "Nurturing"
        )
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = DarkSurfaceVariant,
                    contentColor = TextPrimary,
                    actionColor = NeonCyan
                )
            }
        },
        containerColor = DarkObsidian
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkObsidian)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("avatar_creator_screen")
        ) {
            // Main Navigation Tabs
            TabRow(
                selectedTabIndex = mainScreenTab,
                containerColor = DarkSurface,
                contentColor = NeonMagenta,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[mainScreenTab]),
                        color = NeonMagenta,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = mainScreenTab == 0,
                    onClick = { mainScreenTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Avatar Creator", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    },
                    selectedContentColor = NeonMagenta,
                    unselectedContentColor = TextSecondary,
                    modifier = Modifier.testTag("tab_mode_creator")
                )
                Tab(
                    selected = mainScreenTab == 1,
                    onClick = { mainScreenTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI Chat (${localTraits.name})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    },
                    selectedContentColor = NeonCyan,
                    unselectedContentColor = TextSecondary,
                    modifier = Modifier.testTag("tab_mode_chat")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (mainScreenTab == 1) {
                // GEMINI CHAT INTERFACE
                ChatInterface(
                    avatarTraits = localTraits,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // AVATAR CREATOR CUSTOMIZER MODE
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Top Title & Quick Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Avatar Customizer",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )
                            Text(
                                text = "Live preview & DataStore configuration persistence",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {
                                    localTraits = localTraits.copy(
                                        hairStyle = hairStyles.random(),
                                        hairColor = hairColors.keys.toList().random(),
                                        eyeStyle = eyeStyles.random(),
                                        eyeColor = eyeColors.keys.toList().random(),
                                        clothingStyle = clothingStyles.random(),
                                        clothingColor = clothingColors.keys.toList().random(),
                                        skinTone = skinTones.keys.toList().random(),
                                        expression = expressions.random(),
                                        accessory = accessories.random(),
                                        backgroundStyle = backgroundStyles.random(),
                                        personality = personalities.random()
                                    )
                                    Toast.makeText(context, "Traits randomized!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(DarkSurfaceVariant)
                                    .testTag("randomize_avatar_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Randomize Traits",
                                    tint = NeonCyan
                                )
                            }

                            Button(
                                onClick = {
                                    settingsViewModel.updateAvatarTraits(localTraits)
                                    isSavedNotificationVisible = true
                                    showSaveConfirmationDialog = true
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Avatar '${localTraits.name}' saved to DataStore preferences!")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonMagenta,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("save_avatar_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "Save Avatar",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // PREDEFINED STYLE PRESETS BAR
                    Surface(
                        color = DarkSurface,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Style,
                                        contentDescription = "Style Presets",
                                        tint = NeonMagenta,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Predefined Style Presets",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Text(
                                    text = "Auto-set traits",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(stylePresets) { (presetName, presetTraits) ->
                                    val isCurrentPreset = localTraits.clothingStyle == presetTraits.clothingStyle &&
                                            localTraits.accessory == presetTraits.accessory

                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                localTraits = presetTraits
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Preset '$presetName' applied!")
                                                }
                                            }
                                            .testTag("preset_chip_${presetName.lowercase().replace(" ", "_")}"),
                                        color = if (isCurrentPreset) NeonMagenta.copy(alpha = 0.25f) else DarkSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isCurrentPreset) NeonMagenta else DarkBorder
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = if (isCurrentPreset) NeonMagenta else NeonCyan,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = presetName,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = if (isCurrentPreset) NeonMagenta else TextPrimary,
                                                    fontWeight = if (isCurrentPreset) FontWeight.Bold else FontWeight.Medium,
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // REAL-TIME PREVIEW COMPONENT CARD
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
                            .testTag("realtime_preview_component"),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "Real-time Preview",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Real-Time Avatar Preview",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Row {
                                    IconButton(
                                        onClick = { showFullScreenPreview = true },
                                        modifier = Modifier.testTag("btn_fullscreen_preview")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fullscreen,
                                            contentDescription = "Full Screen Preview",
                                            tint = TextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Name Tag Input Field
                            OutlinedTextField(
                                value = localTraits.name,
                                onValueChange = { localTraits = localTraits.copy(name = it) },
                                label = { Text("Avatar Name") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonMagenta,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedLabelColor = NeonMagenta,
                                    unfocusedLabelColor = TextMuted
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .testTag("avatar_name_input")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Live Canvas Preview Frame
                            Box(
                                modifier = Modifier
                                    .size(230.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, Brush.horizontalGradient(listOf(NeonMagenta, NeonCyan)), CircleShape)
                                    .testTag("avatar_preview_canvas")
                            ) {
                                FemaleAvatarCanvas(
                                    traits = localTraits,
                                    hairColors = hairColors,
                                    eyeColors = eyeColors,
                                    clothingColors = clothingColors,
                                    skinTones = skinTones,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Active Traits Summary Pill Flow
                            FlowRow(
                                horizontalArrangement = Arrangement.Center,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TraitPill(label = localTraits.hairStyle, tint = hairColors[localTraits.hairColor] ?: NeonMagenta)
                                TraitPill(label = localTraits.eyeStyle, tint = eyeColors[localTraits.eyeColor] ?: NeonCyan)
                                TraitPill(label = localTraits.clothingStyle, tint = clothingColors[localTraits.clothingColor] ?: NeonPurple)
                                TraitPill(label = localTraits.personality, tint = NeonCyan)
                                TraitPill(label = localTraits.accessory, tint = TextPrimary)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Direct Action Button to Launch Gemini Chat
                            Button(
                                onClick = { mainScreenTab = 1 },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonPurple,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .testTag("btn_chat_with_avatar_preview")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Chat with ${localTraits.name} via Gemini AI", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Customization Category Tabs
                    TabRow(
                        selectedTabIndex = selectedCategoryTab,
                        containerColor = DarkSurface,
                        contentColor = NeonMagenta,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedCategoryTab]),
                                color = NeonMagenta,
                                height = 3.dp
                            )
                        },
                        divider = { Divider(color = DarkBorder, thickness = 0.5.dp) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    ) {
                        traitCategories.forEachIndexed { index, category ->
                            Tab(
                                selected = selectedCategoryTab == index,
                                onClick = { selectedCategoryTab = index },
                                text = {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (selectedCategoryTab == index) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 12.sp,
                                            color = if (selectedCategoryTab == index) NeonMagenta else TextSecondary
                                        )
                                    )
                                },
                                modifier = Modifier.testTag("avatar_tab_$index")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Trait Selector Content Panel
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
                            when (selectedCategoryTab) {
                                0 -> {
                                    // HAIR CUSTOMIZATION
                                    Text(
                                        text = "Hair Style",
                                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        hairStyles.forEach { style ->
                                            val isSelected = localTraits.hairStyle == style
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { localTraits = localTraits.copy(hairStyle = style) },
                                                label = { Text(style) },
                                                leadingIcon = if (isSelected) {
                                                    { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                                } else null,
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = NeonMagenta,
                                                    selectedLabelColor = Color.White,
                                                    containerColor = DarkSurfaceVariant,
                                                    labelColor = TextSecondary
                                                ),
                                                modifier = Modifier.testTag("hair_style_${style.lowercase().replace(" ", "_")}")
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider(color = DarkBorder, thickness = 0.5.dp)
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Hair Color",
                                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        hairColors.forEach { (colorName, colorVal) ->
                                            val isSelected = localTraits.hairColor == colorName
                                            ColorSwatchButton(
                                                colorName = colorName,
                                                colorValue = colorVal,
                                                isSelected = isSelected,
                                                onClick = { localTraits = localTraits.copy(hairColor = colorName) }
                                            )
                                        }
                                    }
                                }

                                1 -> {
                                    // EYES CUSTOMIZATION
                                    Text(
                                        text = "Eye Style",
                                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        eyeStyles.forEach { style ->
                                            val isSelected = localTraits.eyeStyle == style
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { localTraits = localTraits.copy(eyeStyle = style) },
                                                label = { Text(style) },
                                                leadingIcon = if (isSelected) {
                                                    { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                                } else null,
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = NeonCyan,
                                                    selectedLabelColor = Color.Black,
                                                    containerColor = DarkSurfaceVariant,
                                                    labelColor = TextSecondary
                                                ),
                                                modifier = Modifier.testTag("eye_style_${style.lowercase().replace(" ", "_")}")
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider(color = DarkBorder, thickness = 0.5.dp)
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Iris Color",
                                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        eyeColors.forEach { (colorName, colorVal) ->
                                            val isSelected = localTraits.eyeColor == colorName
                                            ColorSwatchButton(
                                                colorName = colorName,
                                                colorValue = colorVal,
                                                isSelected = isSelected,
                                                onClick = { localTraits = localTraits.copy(eyeColor = colorName) }
                                            )
                                        }
                                    }
                                }

                                2 -> {
                                    // CLOTHING CUSTOMIZATION
                                    Text(
                                        text = "Outfit Style",
                                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        clothingStyles.forEach { style ->
                                            val isSelected = localTraits.clothingStyle == style
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { localTraits = localTraits.copy(clothingStyle = style) },
                                                label = { Text(style) },
                                                leadingIcon = if (isSelected) {
                                                    { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                                } else null,
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = NeonPurple,
                                                    selectedLabelColor = Color.White,
                                                    containerColor = DarkSurfaceVariant,
                                                    labelColor = TextSecondary
                                                ),
                                                modifier = Modifier.testTag("clothing_style_${style.lowercase().replace(" ", "_")}")
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider(color = DarkBorder, thickness = 0.5.dp)
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Clothing Color",
                                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        clothingColors.forEach { (colorName, colorVal) ->
                                            val isSelected = localTraits.clothingColor == colorName
                                            ColorSwatchButton(
                                                colorName = colorName,
                                                colorValue = colorVal,
                                                isSelected = isSelected,
                                                onClick = { localTraits = localTraits.copy(clothingColor = colorName) }
                                            )
                                        }
                                    }
                                }

                                3 -> {
                                    // SKIN & FACE CUSTOMIZATION
                                    Text(
                                        text = "Skin Tone",
                                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        skinTones.forEach { (toneName, colorVal) ->
                                            val isSelected = localTraits.skinTone == toneName
                                            ColorSwatchButton(
                                                colorName = toneName,
                                                colorValue = colorVal,
                                                isSelected = isSelected,
                                                onClick = { localTraits = localTraits.copy(skinTone = toneName) }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider(color = DarkBorder, thickness = 0.5.dp)
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Facial Expression",
                                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        expressions.forEach { expr ->
                                            val isSelected = localTraits.expression == expr
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { localTraits = localTraits.copy(expression = expr) },
                                                label = { Text(expr) },
                                                leadingIcon = if (isSelected) {
                                                    { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                                } else null,
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = NeonMagenta,
                                                    selectedLabelColor = Color.White,
                                                    containerColor = DarkSurfaceVariant,
                                                    labelColor = TextSecondary
                                                ),
                                                modifier = Modifier.testTag("expression_${expr.lowercase().replace(" ", "_")}")
                                            )
                                        }
                                    }
                                }

                                4 -> {
                                    // EXTRAS & ACCESSORIES & PERSONALITY
                                    Text(
                                        text = "Personality & Roleplay Archetype",
                                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Shapes how Gemini AI talks, reacts, and roleplays in chat",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        personalities.forEach { p ->
                                            val isSelected = localTraits.personality == p
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { localTraits = localTraits.copy(personality = p) },
                                                label = { Text(p) },
                                                leadingIcon = if (isSelected) {
                                                    { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                                } else null,
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = NeonMagenta,
                                                    selectedLabelColor = Color.White,
                                                    containerColor = DarkSurfaceVariant,
                                                    labelColor = TextSecondary
                                                ),
                                                modifier = Modifier.testTag("personality_${p.lowercase()}")
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider(color = DarkBorder, thickness = 0.5.dp)
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Accessories",
                                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        accessories.forEach { acc ->
                                            val isSelected = localTraits.accessory == acc
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { localTraits = localTraits.copy(accessory = acc) },
                                                label = { Text(acc) },
                                                leadingIcon = if (isSelected) {
                                                    { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                                } else null,
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = NeonCyan,
                                                    selectedLabelColor = Color.Black,
                                                    containerColor = DarkSurfaceVariant,
                                                    labelColor = TextSecondary
                                                ),
                                                modifier = Modifier.testTag("accessory_${acc.lowercase().replace(" ", "_")}")
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider(color = DarkBorder, thickness = 0.5.dp)
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Background Style",
                                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        backgroundStyles.forEach { bg ->
                                            val isSelected = localTraits.backgroundStyle == bg
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { localTraits = localTraits.copy(backgroundStyle = bg) },
                                                label = { Text(bg) },
                                                leadingIcon = if (isSelected) {
                                                    { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                                } else null,
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = NeonPurple,
                                                    selectedLabelColor = Color.White,
                                                    containerColor = DarkSurfaceVariant,
                                                    labelColor = TextSecondary
                                                ),
                                                modifier = Modifier.testTag("bg_style_${bg.lowercase().replace(" ", "_")}")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // SAVE CONFIRMATION DIALOG
    if (showSaveConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showSaveConfirmationDialog = false },
            containerColor = DarkSurface,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Avatar Configuration Saved", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Your avatar traits for '${localTraits.name}' have been serialized & persisted to DataStore preferences.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 13.sp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, NeonCyan, CircleShape)
                    ) {
                        FemaleAvatarCanvas(
                            traits = localTraits,
                            hairColors = hairColors,
                            eyeColors = eyeColors,
                            clothingColors = clothingColors,
                            skinTones = skinTones,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${localTraits.hairStyle} (${localTraits.hairColor}) • ${localTraits.clothingStyle}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSaveConfirmationDialog = false
                        mainScreenTab = 1 // Switch to Gemini Chat
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
                ) {
                    Text("Chat with Avatar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSaveConfirmationDialog = false }
                ) {
                    Text("Close", color = TextSecondary)
                }
            }
        )
    }

    // FULL SCREEN PREVIEW MODAL DIALOG
    if (showFullScreenPreview) {
        AlertDialog(
            onDismissRequest = { showFullScreenPreview = false },
            containerColor = DarkObsidian,
            titleContentColor = TextPrimary,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(localTraits.name, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = NeonMagenta)
                    TextButton(onClick = { showFullScreenPreview = false }) {
                        Text("Close", color = TextSecondary)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .clip(CircleShape)
                            .border(4.dp, Brush.horizontalGradient(listOf(NeonMagenta, NeonCyan)), CircleShape)
                    ) {
                        FemaleAvatarCanvas(
                            traits = localTraits,
                            hairColors = hairColors,
                            eyeColors = eyeColors,
                            clothingColors = clothingColors,
                            skinTones = skinTones,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurface, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text("Trait Configuration Breakdown", fontWeight = FontWeight.Bold, color = NeonCyan, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• Hair: ${localTraits.hairStyle} (${localTraits.hairColor})", color = TextSecondary, fontSize = 12.sp)
                        Text("• Eyes: ${localTraits.eyeStyle} (${localTraits.eyeColor})", color = TextSecondary, fontSize = 12.sp)
                        Text("• Outfit: ${localTraits.clothingStyle} (${localTraits.clothingColor})", color = TextSecondary, fontSize = 12.sp)
                        Text("• Skin & Expression: ${localTraits.skinTone} - ${localTraits.expression}", color = TextSecondary, fontSize = 12.sp)
                        Text("• Accessory & Vibe: ${localTraits.accessory} - ${localTraits.backgroundStyle}", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun ColorSwatchButton(
    colorName: String,
    colorValue: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) colorValue.copy(alpha = 0.25f) else DarkSurfaceVariant)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) colorValue else DarkBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("color_swatch_$colorName")
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(colorValue)
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = colorName,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isSelected) TextPrimary else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun TraitPill(label: String, tint: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tint.copy(alpha = 0.15f))
            .border(0.5.dp, tint.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = tint,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        )
    }
}

/**
 * Custom Canvas painter that visually renders the customized female avatar in real-time.
 */
@Composable
private fun FemaleAvatarCanvas(
    traits: AvatarTraits,
    hairColors: Map<String, Color>,
    eyeColors: Map<String, Color>,
    clothingColors: Map<String, Color>,
    skinTones: Map<String, Color>,
    modifier: Modifier = Modifier
) {
    val skinColor = skinTones[traits.skinTone] ?: Color(0xFFFFF0E5)
    val hairColor = hairColors[traits.hairColor] ?: Color(0xFFEC4899)
    val eyeColor = eyeColors[traits.eyeColor] ?: Color(0xFF2563EB)
    val clothingColor = clothingColors[traits.clothingColor] ?: Color(0xFF7C3AED)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerX = w / 2f
        val centerY = h / 2f

        // 1. DRAW BACKGROUND STYLE
        when (traits.backgroundStyle) {
            "Neon Grid" -> {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF2E1065), Color(0xFF09090B)),
                        center = Offset(centerX, centerY),
                        radius = w * 0.7f
                    )
                )
                // Grid lines
                for (i in 0..10) {
                    val x = w * (i / 10f)
                    drawLine(Color(0x3006B6D4), Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                    val y = h * (i / 10f)
                    drawLine(Color(0x3006B6D4), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                }
            }
            "Cyber City Night" -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF3B0764), Color(0xFF020617))
                    )
                )
            }
            "Sunset Glow" -> {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFF59E0B), Color(0xFFEC4899), Color(0xFF4C1D95))
                    )
                )
            }
            "Pastel Aura" -> {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFBCFE8), Color(0xFFA5F3FC), Color(0xFF0F172A)),
                        center = Offset(centerX, centerY),
                        radius = w * 0.75f
                    )
                )
            }
            else -> { // Abstract Void
                drawRect(Color(0xFF09090B))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(hairColor.copy(alpha = 0.3f), Color.Transparent)
                    ),
                    radius = w * 0.45f,
                    center = Offset(centerX, centerY)
                )
            }
        }

        // 2. BACK HAIR (for long styles or twin tails)
        when (traits.hairStyle) {
            "Long Waves" -> {
                val path = Path().apply {
                    moveTo(centerX - 80f, centerY - 20f)
                    cubicTo(centerX - 100f, centerY + 60f, centerX - 90f, centerY + 110f, centerX - 70f, h)
                    lineTo(centerX + 70f, h)
                    cubicTo(centerX + 90f, centerY + 110f, centerX + 100f, centerY + 60f, centerX + 80f, centerY - 20f)
                    close()
                }
                drawPath(path, color = hairColor)
            }
            "Anime Twin Tails" -> {
                // Left tail
                val leftTail = Path().apply {
                    moveTo(centerX - 50f, centerY - 60f)
                    cubicTo(centerX - 120f, centerY - 20f, centerX - 130f, centerY + 60f, centerX - 100f, centerY + 110f)
                    cubicTo(centerX - 80f, centerY + 70f, centerX - 60f, centerY, centerX - 40f, centerY - 30f)
                    close()
                }
                drawPath(leftTail, color = hairColor)

                // Right tail
                val rightTail = Path().apply {
                    moveTo(centerX + 50f, centerY - 60f)
                    cubicTo(centerX + 120f, centerY - 20f, centerX + 130f, centerY + 60f, centerX + 100f, centerY + 110f)
                    cubicTo(centerX + 80f, centerY + 70f, centerX + 60f, centerY, centerX + 40f, centerY - 30f)
                    close()
                }
                drawPath(rightTail, color = hairColor)
            }
            "Braids" -> {
                drawCircle(hairColor, radius = 25f, center = Offset(centerX - 75f, centerY + 60f))
                drawCircle(hairColor, radius = 25f, center = Offset(centerX + 75f, centerY + 60f))
            }
            "Afro Buns" -> {
                drawCircle(hairColor, radius = 35f, center = Offset(centerX - 65f, centerY - 65f))
                drawCircle(hairColor, radius = 35f, center = Offset(centerX + 65f, centerY - 65f))
            }
        }

        // 3. CLOTHING / OUTFIT (SHICK & SHOULDERS)
        val clothingPath = Path().apply {
            moveTo(centerX - 30f, centerY + 50f)
            lineTo(centerX - 100f, h)
            lineTo(centerX + 100f, h)
            lineTo(centerX + 30f, centerY + 50f)
            close()
        }
        drawPath(clothingPath, color = clothingColor)

        // Clothing accent detail
        when (traits.clothingStyle) {
            "Cyber Jacket" -> {
                drawLine(Color(0xFF06B6D4), Offset(centerX - 20f, centerY + 50f), Offset(centerX - 80f, h), strokeWidth = 4f)
                drawLine(Color(0xFF06B6D4), Offset(centerX + 20f, centerY + 50f), Offset(centerX + 80f, h), strokeWidth = 4f)
            }
            "Elegant Kimono" -> {
                val vNeck = Path().apply {
                    moveTo(centerX - 30f, centerY + 50f)
                    lineTo(centerX, centerY + 80f)
                    lineTo(centerX + 30f, centerY + 50f)
                }
                drawPath(vNeck, color = Color.White.copy(alpha = 0.8f), style = Stroke(width = 6f))
            }
            "Casual Hoodie" -> {
                drawCircle(Color.White, radius = 4f, center = Offset(centerX - 10f, centerY + 75f))
                drawCircle(Color.White, radius = 4f, center = Offset(centerX + 10f, centerY + 75f))
                drawLine(Color.White, Offset(centerX - 10f, centerY + 75f), Offset(centerX - 10f, centerY + 95f), strokeWidth = 2f)
                drawLine(Color.White, Offset(centerX + 10f, centerY + 75f), Offset(centerX + 10f, centerY + 95f), strokeWidth = 2f)
            }
        }

        // 4. NECK
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX - 18f, centerY + 25f),
            size = Size(36f, 30f)
        )

        // 5. FACE HEAD SHAPE (ELEGANT OVAL)
        val headPath = Path().apply {
            moveTo(centerX, centerY - 70f)
            cubicTo(centerX + 55f, centerY - 70f, centerX + 50f, centerY + 15f, centerX + 25f, centerY + 45f)
            cubicTo(centerX + 10f, centerY + 55f, centerX - 10f, centerY + 55f, centerX - 25f, centerY + 45f)
            cubicTo(centerX - 50f, centerY + 15f, centerX - 55f, centerY - 70f, centerX, centerY - 70f)
            close()
        }
        drawPath(headPath, color = skinColor)

        // Blush Cheeks
        drawCircle(Color(0x30EC4899), radius = 12f, center = Offset(centerX - 30f, centerY + 10f))
        drawCircle(Color(0x30EC4899), radius = 12f, center = Offset(centerX + 30f, centerY + 10f))

        // 6. EYES
        val leftEyeCenter = Offset(centerX - 22f, centerY - 10f)
        val rightEyeCenter = Offset(centerX + 22f, centerY - 10f)

        fun drawSingleEye(center: Offset, isWinking: Boolean = false) {
            if (isWinking) {
                val winkPath = Path().apply {
                    moveTo(center.x - 12f, center.y)
                    cubicTo(center.x - 6f, center.y + 8f, center.x + 6f, center.y + 8f, center.x + 12f, center.y)
                }
                drawPath(winkPath, color = Color(0xFF18181B), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
                return
            }

            // Sclera
            drawOval(Color.White, topLeft = Offset(center.x - 12f, center.y - 10f), size = Size(24f, 20f))
            // Iris
            drawCircle(eyeColor, radius = 7f, center = center)
            // Pupil
            drawCircle(Color(0xFF09090B), radius = 3.5f, center = center)
            // Sparkle sheen
            drawCircle(Color.White, radius = 2f, center = Offset(center.x - 2.5f, center.y - 2.5f))

            // Upper Eyelash / Eyeliner
            val lashPath = Path().apply {
                moveTo(center.x - 14f, center.y - 8f)
                cubicTo(center.x - 4f, center.y - 14f, center.x + 4f, center.y - 14f, center.x + 14f, center.y - 8f)
            }
            drawPath(lashPath, color = Color(0xFF18181B), style = Stroke(width = 3.5f, cap = StrokeCap.Round))

            if (traits.eyeStyle == "Cat Eye") {
                drawLine(Color(0xFF18181B), Offset(center.x + 12f, center.y - 8f), Offset(center.x + 18f, center.y - 13f), strokeWidth = 3f)
            } else if (traits.eyeStyle == "Cybernetic Ring") {
                drawCircle(NeonCyan, radius = 9f, center = center, style = Stroke(width = 1.5f))
            }
        }

        val isRightWinking = traits.expression == "Playful Wink"
        drawSingleEye(leftEyeCenter)
        drawSingleEye(rightEyeCenter, isWinking = isRightWinking)

        // Eyebrows
        val eyebrowY = centerY - 24f
        when (traits.expression) {
            "Fierce" -> {
                drawLine(Color(0xFF27272A), Offset(centerX - 32f, eyebrowY - 2f), Offset(centerX - 12f, eyebrowY + 3f), strokeWidth = 2.5f)
                drawLine(Color(0xFF27272A), Offset(centerX + 12f, eyebrowY + 3f), Offset(centerX + 32f, eyebrowY - 2f), strokeWidth = 2.5f)
            }
            else -> {
                val leftBrow = Path().apply {
                    moveTo(centerX - 32f, eyebrowY + 1f)
                    cubicTo(centerX - 24f, eyebrowY - 4f, centerX - 16f, eyebrowY - 4f, centerX - 12f, eyebrowY + 1f)
                }
                val rightBrow = Path().apply {
                    moveTo(centerX + 12f, eyebrowY + 1f)
                    cubicTo(centerX + 16f, eyebrowY - 4f, centerX + 24f, eyebrowY - 4f, centerX + 32f, eyebrowY + 1f)
                }
                drawPath(leftBrow, color = Color(0xFF27272A), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
                drawPath(rightBrow, color = Color(0xFF27272A), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
            }
        }

        // 7. NOSE
        val nosePath = Path().apply {
            moveTo(centerX, centerY)
            lineTo(centerX - 2f, centerY + 8f)
            lineTo(centerX + 2f, centerY + 8f)
        }
        drawPath(nosePath, color = Color(0x508B5CF6), style = Stroke(width = 2f, cap = StrokeCap.Round))

        // 8. MOUTH / EXPRESSION
        val mouthY = centerY + 26f
        when (traits.expression) {
            "Confident Smile", "Playful Wink" -> {
                val mouthPath = Path().apply {
                    moveTo(centerX - 12f, mouthY - 2f)
                    cubicTo(centerX - 6f, mouthY + 10f, centerX + 6f, mouthY + 10f, centerX + 12f, mouthY - 2f)
                }
                drawPath(mouthPath, color = Color(0xFFDC2626), style = Stroke(width = 3f, cap = StrokeCap.Round))
            }
            "Serene" -> {
                val mouthPath = Path().apply {
                    moveTo(centerX - 8f, mouthY)
                    cubicTo(centerX - 4f, mouthY + 4f, centerX + 4f, mouthY + 4f, centerX + 8f, mouthY)
                }
                drawPath(mouthPath, color = Color(0xFFEC4899), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
            }
            "Thoughtful" -> {
                drawLine(Color(0xFF18181B), Offset(centerX - 6f, mouthY + 2f), Offset(centerX + 6f, mouthY - 2f), strokeWidth = 2.5f)
            }
            "Fierce" -> {
                val mouthPath = Path().apply {
                    moveTo(centerX - 10f, mouthY + 4f)
                    cubicTo(centerX - 5f, mouthY - 2f, centerX + 5f, mouthY - 2f, centerX + 10f, mouthY + 4f)
                }
                drawPath(mouthPath, color = Color(0xFF991B1B), style = Stroke(width = 3f, cap = StrokeCap.Round))
            }
        }

        // 9. FRONT BANGS / FRONT HAIR
        val bangsPath = Path().apply {
            moveTo(centerX - 55f, centerY - 35f)
            cubicTo(centerX - 30f, centerY - 75f, centerX + 30f, centerY - 75f, centerX + 55f, centerY - 35f)
            // Bang strands
            cubicTo(centerX + 40f, centerY - 45f, centerX + 25f, centerY - 30f, centerX + 15f, centerY - 45f)
            cubicTo(centerX, centerY - 25f, centerX - 15f, centerY - 45f, centerX - 30f, centerY - 35f)
            close()
        }
        drawPath(bangsPath, color = hairColor)

        // Hair Sheen highlight
        val sheenPath = Path().apply {
            moveTo(centerX - 35f, centerY - 55f)
            cubicTo(centerX - 10f, centerY - 65f, centerX + 10f, centerY - 65f, centerX + 35f, centerY - 55f)
        }
        drawPath(sheenPath, color = Color.White.copy(alpha = 0.4f), style = Stroke(width = 4f, cap = StrokeCap.Round))

        // 10. ACCESSORIES OVERLAY
        when (traits.accessory) {
            "Cyber Visor" -> {
                val visorPath = Path().apply {
                    moveTo(centerX - 42f, centerY - 18f)
                    lineTo(centerX + 42f, centerY - 18f)
                    lineTo(centerX + 36f, centerY - 2f)
                    lineTo(centerX - 36f, centerY - 2f)
                    close()
                }
                drawPath(visorPath, color = Color(0xDD06B6D4))
                drawPath(visorPath, color = NeonCyan, style = Stroke(width = 2f))
            }
            "Cat Ear Headset" -> {
                // Headband
                val band = Path().apply {
                    moveTo(centerX - 45f, centerY - 45f)
                    cubicTo(centerX - 20f, centerY - 72f, centerX + 20f, centerY - 72f, centerX + 45f, centerY - 45f)
                }
                drawPath(band, color = Color(0xFF18181B), style = Stroke(width = 6f))

                // Left Ear
                val leftEar = Path().apply {
                    moveTo(centerX - 40f, centerY - 55f)
                    lineTo(centerX - 55f, centerY - 85f)
                    lineTo(centerX - 25f, centerY - 65f)
                    close()
                }
                drawPath(leftEar, color = hairColor)
                drawPath(leftEar, color = NeonMagenta, style = Stroke(width = 2f))

                // Right Ear
                val rightEar = Path().apply {
                    moveTo(centerX + 40f, centerY - 55f)
                    lineTo(centerX + 55f, centerY - 85f)
                    lineTo(centerX + 25f, centerY - 65f)
                    close()
                }
                drawPath(rightEar, color = hairColor)
                drawPath(rightEar, color = NeonMagenta, style = Stroke(width = 2f))
            }
            "Round Glasses" -> {
                drawCircle(Color(0xFFD1D5DB), radius = 14f, center = Offset(centerX - 22f, centerY - 10f), style = Stroke(width = 2.5f))
                drawCircle(Color(0xFFD1D5DB), radius = 14f, center = Offset(centerX + 22f, centerY - 10f), style = Stroke(width = 2.5f))
                drawLine(Color(0xFFD1D5DB), Offset(centerX - 8f, centerY - 10f), Offset(centerX + 8f, centerY - 10f), strokeWidth = 2.5f)
            }
            "Gold Earrings" -> {
                drawCircle(Color(0xFFF59E0B), radius = 5f, center = Offset(centerX - 45f, centerY + 15f))
                drawCircle(Color(0xFFF59E0B), radius = 5f, center = Offset(centerX + 45f, centerY + 15f))
            }
            "Star Hairpin" -> {
                drawCircle(Color(0xFFF59E0B), radius = 6f, center = Offset(centerX + 30f, centerY - 40f))
            }
        }
    }
}
