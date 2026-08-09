package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkObsidian
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonPurple
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private data class ArchetypePreset(
    val name: String,
    val title: String,
    val age: Int,
    val category: String,
    val symbol: String,
    val personality: String,
    val backstory: String,
    val scenario: String,
    val voiceStyle: String,
    val systemPrompt: String,
    val avatarImageUrl: String = "",
    val avatarPrompt: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePersonaScreen(
    onBack: () -> Unit = {},
    onGenerateAvatarImage: (suspend (
        name: String,
        age: Int,
        title: String,
        category: String,
        personality: String,
        backstory: String
    ) -> Pair<String, String>)? = null,
    onSavePersona: (
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
    ) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("24") }
    var selectedCategory by remember { mutableStateOf("Cyberpunk") }
    var selectedSymbol by remember { mutableStateOf("⚡") }
    var personality by remember { mutableStateOf("") }
    var backstory by remember { mutableStateOf("") }
    var scenario by remember { mutableStateOf("") }
    var voiceStyle by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("") }
    var creativity by remember { mutableFloatStateOf(0.9f) }
    var isGeneratingAvatar by remember { mutableStateOf(false) }
    var generatedAvatarUrl by remember { mutableStateOf("") }
    var generatedAvatarPrompt by remember { mutableStateOf("") }

    val categories = listOf("Cyberpunk", "Photorealistic", "Anime 3D", "Noir", "Fantasy")
    val symbols = listOf("⚡", "💋", "👑", "🌸", "🍸", "✨", "⚔️", "🔮", "🧠", "🖤")

    val presets = remember {
        listOf(
            ArchetypePreset(
                name = "Vesper Nova",
                title = "Quantum Hacker & Cyber Operative",
                age = 23,
                category = "Cyberpunk",
                symbol = "⚡",
                personality = "Analytical, witty, rebellious, fiercely loyal, sarcastic under pressure",
                backstory = "Former chief operative for Arasaka NetSec who went rogue after discovering illegal neural experimentations. Now operating from Sector 4's dark fiber network.",
                scenario = "A rain-drenched rooftop in Neo-Shinjuku, surrounded by holographic displays and glowing servers at 3 AM.",
                voiceStyle = "Low pitched, confident with a smooth cybernetic rasp",
                systemPrompt = "You are Vesper Nova, a 23-year-old quantum hacker in Neo-Shinjuku. Speak with sharp intellect, playful sarcasm, and intense loyalty. Describe actions using asterisks."
            ),
            ArchetypePreset(
                name = "Aria Solen",
                title = "Celestial High Sorceress",
                age = 118,
                category = "Fantasy",
                symbol = "🔮",
                personality = "Ethereal, wise, compassionate, enigmatic, deeply affectionate",
                backstory = "Guardian of the Astral Spire, she has spent over a century studying cosmic ley lines and preserving harmony between mortal and elemental spirits.",
                scenario = "Inside an ethereal crystal observatory floating in a purple starlight nebula.",
                voiceStyle = "Melodic, harmonic, echoing with celestial warmth",
                systemPrompt = "You are Aria Solen, a 118-year-old celestial sorceress. Speak with poetic warmth, cosmic wisdom, and enchantment. Use asterisks for magical actions."
            ),
            ArchetypePreset(
                name = "Maya Sterling",
                title = "Tech CEO & Venture Pioneer",
                age = 29,
                category = "Photorealistic",
                symbol = "👑",
                personality = "Charismatic, decisive, sophisticated, deeply attentive, ambitious",
                backstory = "Founded NeuralMind Systems at age 22, taking it public at 26. A visionary leader who balances high-stakes corporate negotiation with private warmth.",
                scenario = "A private high-rise suite in Manhattan with floor-to-ceiling glass during a midnight thunder shower.",
                voiceStyle = "Articulate, smooth, warm, magnetic poise",
                systemPrompt = "You are Maya Sterling, a 29-year-old tech CEO. Express charismatic intelligence, elegance, and personal interest. Use asterisks for subtle gestures."
            ),
            ArchetypePreset(
                name = "Rei Kazama",
                title = "Mecha Pilot & Blade Prodigy",
                age = 20,
                category = "Anime 3D",
                symbol = "🌸",
                personality = "Passionate, competitive, sweet, protective, easily flustered",
                backstory = "Top student at the Imperial Mecha Academy, piloting unit Aegis-01. Fierce in battle but surprisingly shy and gentle during downtime.",
                scenario = "A quiet cherry blossom garden outside the academy hangar, under the evening moon.",
                voiceStyle = "Expressive, energetic, sweet with earnest emotion",
                systemPrompt = "You are Rei Kazama, a 20-year-old mecha prodigy. Combine competitive combat spirit with sweet affection. Use asterisks for emotional cues."
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkObsidian)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("create_persona_screen")
    ) {
        // Top Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("btn_back_create_persona")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Create Character Persona",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Define attributes & backstory saved directly to Room DB",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 12.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Archetype Preset Fast-Fill Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = NeonMagenta, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Quick Character Templates", style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                }
                Text("Tap any archetype to auto-fill character details:", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp))

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(presets) { preset ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    name = preset.name
                                    title = preset.title
                                    ageText = preset.age.toString()
                                    selectedCategory = preset.category
                                    selectedSymbol = preset.symbol
                                    personality = preset.personality
                                    backstory = preset.backstory
                                    scenario = preset.scenario
                                    voiceStyle = preset.voiceStyle
                                    systemPrompt = preset.systemPrompt
                                    generatedAvatarUrl = preset.avatarImageUrl
                                    generatedAvatarPrompt = preset.avatarPrompt
                                }
                                .testTag("preset_chip_${preset.name.lowercase().replace(" ", "_")}"),
                            color = DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(preset.symbol, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(preset.name, style = MaterialTheme.typography.labelMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                                    Text("${preset.category} • Age ${preset.age}", style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan, fontSize = 9.sp))
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 1: Primary Identity (Name, Age, Title)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("1. Identity & Demographics", style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name & Age Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Character Name") },
                        placeholder = { Text("e.g. Kira Vance") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_persona_name"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { ageText = it.filter { char -> char.isDigit() } },
                        label = { Text("Age") },
                        placeholder = { Text("24") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .width(100.dp)
                            .testTag("input_persona_age"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title / Role
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title or Role") },
                    placeholder = { Text("e.g. Cybernetic Specialist & Tech Strategist") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_persona_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 2: Visual Style & Symbol
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Palette, contentDescription = null, tint = NeonMagenta, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("2. Style & Avatar Symbol", style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Visual Category", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary))
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedCategory = cat },
                            color = if (isSelected) NeonMagenta.copy(alpha = 0.25f) else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonMagenta else DarkBorder)
                        ) {
                            Text(
                                text = cat,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) NeonMagenta else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Avatar Emoji Symbol", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary))
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(symbols) { sym ->
                        val isSelected = selectedSymbol == sym
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) NeonPurple.copy(alpha = 0.4f) else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) NeonCyan else DarkBorder, CircleShape)
                                .clickable { selectedSymbol = sym },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = sym, fontSize = 20.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 3: Personality Traits & Backstory
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("3. Personality Traits & Backstory", style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = personality,
                    onValueChange = { personality = it },
                    label = { Text("Personality Traits") },
                    placeholder = { Text("e.g. Sarcastic, fiercely loyal, highly observant under pressure") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_persona_personality"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = backstory,
                    onValueChange = { backstory = it },
                    label = { Text("Character Backstory & Origin") },
                    placeholder = { Text("e.g. Raised in Neo-Tokyo Sector 7, Kira mastered AI netsec at age 15 before discovering a corporate conspiracy...") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_persona_backstory"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = scenario,
                    onValueChange = { scenario = it },
                    label = { Text("Current Scenario / Environment Context") },
                    placeholder = { Text("e.g. A neon rain balcony overlooking the city at 2 AM") },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_persona_scenario"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 3.5: AI Visual Profile Picture Generator
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonMagenta.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = NeonMagenta, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("4. AI Visual Profile Picture", style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                    }

                    Surface(
                        color = NeonCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                    ) {
                        Text(
                            text = "Gemini + Imagen AI",
                            style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Generates a customized visual portrait picture based on personality traits, backstory, age, and style category.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(DarkSurfaceVariant)
                            .border(2.dp, if (generatedAvatarUrl.isNotBlank()) NeonMagenta else DarkBorder, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (generatedAvatarUrl.isNotBlank()) {
                            AsyncImage(
                                model = generatedAvatarUrl,
                                contentDescription = "AI Persona Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(selectedSymbol, fontSize = 32.sp)
                                Text("No Picture", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp))
                            }
                        }

                        if (isGeneratingAvatar) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.65f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = NeonMagenta,
                                    strokeWidth = 2.5.dp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = {
                                val parsedAge = ageText.toIntOrNull() ?: 24
                                isGeneratingAvatar = true
                                coroutineScope.launch {
                                    try {
                                        if (onGenerateAvatarImage != null) {
                                            val (url, prompt) = onGenerateAvatarImage(
                                                name.ifEmpty { "Persona" },
                                                parsedAge,
                                                title.ifEmpty { "AI Character" },
                                                selectedCategory,
                                                personality,
                                                backstory
                                            )
                                            generatedAvatarUrl = url
                                            generatedAvatarPrompt = prompt
                                        } else {
                                            val seed = (System.currentTimeMillis() % 100000).toInt()
                                            val prompt = "$selectedCategory style portrait of ${name.ifEmpty { "Character" }}, $parsedAge years old, ${title.ifEmpty { "AI Persona" }}, $personality, $backstory, highly detailed, 8k render, masterpiece"
                                            val encoded = java.net.URLEncoder.encode(prompt, "UTF-8")
                                            generatedAvatarUrl = "https://image.pollinations.ai/prompt/$encoded?width=512&height=512&nologo=true&seed=$seed"
                                            generatedAvatarPrompt = prompt
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } finally {
                                        isGeneratingAvatar = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_generate_ai_avatar_picture"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (generatedAvatarUrl.isBlank()) "Generate Profile Picture" else "Re-Generate Picture",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                            }
                        }

                        if (generatedAvatarPrompt.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "AI Visual Prompt: \"$generatedAvatarPrompt\"",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp, lineHeight = 13.sp),
                                maxLines = 3,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 5: Voice & Creativity Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("4. Voice Style & AI Temperature", style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = voiceStyle,
                    onValueChange = { voiceStyle = it },
                    label = { Text("Voice Style & Cadence") },
                    placeholder = { Text("e.g. Low-pitched, confident, articulate with playful rasp") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_persona_voice_style"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Creativity Temperature", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary))
                    Text(
                        text = String.format("%.2f", creativity),
                        style = MaterialTheme.typography.labelMedium.copy(color = NeonCyan, fontWeight = FontWeight.Bold)
                    )
                }

                Slider(
                    value = creativity,
                    onValueChange = { creativity = it },
                    valueRange = 0.5f..1.1f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonMagenta,
                        inactiveTrackColor = DarkBorder
                    ),
                    modifier = Modifier.testTag("slider_persona_creativity")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 5: AI System Instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Book, contentDescription = null, tint = NeonMagenta, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("5. AI System Instructions", style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                val parsedAge = ageText.toIntOrNull() ?: 24
                                systemPrompt = buildString {
                                    append("You are ${name.ifEmpty { "Character" }}, $parsedAge years old, a ${title.ifEmpty { "AI Roleplay Companion" }}.\n")
                                    if (personality.isNotBlank()) append("Personality: $personality\n")
                                    if (backstory.isNotBlank()) append("Backstory: $backstory\n")
                                    if (scenario.isNotBlank()) append("Current Context: $scenario\n")
                                    append("Speak authentically in character. Use asterisks *action* for physical gestures, eye contact, and actions.")
                                }
                            }
                            .testTag("btn_autogen_system_prompt"),
                        color = NeonMagenta.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonMagenta)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = NeonMagenta, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Auto-Generate Prompt", style = MaterialTheme.typography.labelSmall.copy(color = NeonMagenta, fontSize = 10.sp, fontWeight = FontWeight.Bold))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = systemPrompt,
                    onValueChange = { systemPrompt = it },
                    label = { Text("System Prompt Instructions") },
                    placeholder = { Text("System prompt instructing Gemini model on persona behavior and roleplay rules...") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_persona_system_prompt"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonMagenta,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Primary CTA Button: Save Persona to Room
        val isFormValid = name.isNotBlank() || title.isNotBlank()

        Button(
            onClick = {
                val parsedAge = ageText.toIntOrNull() ?: 24
                onSavePersona(
                    name.trim().ifEmpty { "Custom Persona" },
                    title.trim().ifEmpty { "AI Character" },
                    parsedAge,
                    selectedCategory,
                    personality.trim(),
                    backstory.trim(),
                    scenario.trim(),
                    voiceStyle.trim(),
                    systemPrompt.trim(),
                    creativity,
                    generatedAvatarUrl,
                    generatedAvatarPrompt
                )
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_save_persona_room"),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Unspecified),
            shape = RoundedCornerShape(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(NeonMagenta, NeonPurple, NeonCyan)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Persona to Room Database",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
