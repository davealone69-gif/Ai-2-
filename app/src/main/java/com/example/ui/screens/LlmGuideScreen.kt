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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LlmRecommendationCard
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
fun LlmGuideScreen() {
    var selectedTab by remember { mutableStateOf("Roleplay LLMs") }
    val tabs = listOf("Roleplay LLMs", "Avatar Engines", "Model Creation", "Local Hosting")

    val roleplayLlms = remember {
        listOf(
            Triple(
                "Llama-3.3-70B-Instruct",
                "Meta Architecture • 70 Billion Parameters",
                "Unmatched reasoning and deep long-term memory. Fine-tunes excel at multi-turn emotional roleplay, subtle romance, and dramatic tension without losing persona alignment."
            ) to Pair(
                listOf("Complex multi-turn dialogue memory", "High nuance & emotional depth", "Consistent character adherence"),
                "Temp: 0.90 • Top_P: 0.95 • Repetition Penalty: 1.08" to "16GB+ VRAM (Quantized)"
            ),
            Triple(
                "Mistral-NeMo-12B (Uncensored)",
                "Mistral AI • 12 Billion Parameters",
                "Extremely fast and creative. Fine-tuned variants completely remove guardrails and refusal behavior, enabling unrestricted storytelling and vivid action descriptions."
            ) to Pair(
                listOf("Zero refusal guardrails", "Fast response times", "Vivid asterisk action descriptions"),
                "Temp: 0.95 • Top_P: 0.92 • Repetition Penalty: 1.12" to "8GB VRAM"
            ),
            Triple(
                "Mythomax-L2-13B",
                "Community Hybrid • 13 Billion Parameters",
                "Gold standard for classic storytelling & roleplay. Combines Llama 2 fine-tunes for optimal balance between descriptive prose and conversational allure."
            ) to Pair(
                listOf("Rich narrative prose", "Natural dialogue cadence", "Highly adaptable to custom prompts"),
                "Temp: 0.85 • Top_P: 0.90 • Repetition Penalty: 1.10" to "10GB VRAM"
            ),
            Triple(
                "Midnight-Rose-70B",
                "Llama-3 Fine-Tune • 70B",
                "Purposely trained on millions of romance, fantasy, and character interaction turns for deep, engaging roleplay with rich emotional resonance."
            ) to Pair(
                listOf("Romance & dramatic roleplay specialization", "Natural conversational tone", "High context window"),
                "Temp: 0.92 • Top_P: 0.95 • Repetition Penalty: 1.06" to "24GB VRAM"
            )
        )
    }

    val avatarEngines = remember {
        listOf(
            Triple(
                "Flux.1 Dev / Schnell",
                "Black Forest Labs • Next-Gen Diffusion",
                "State-of-the-art image generation engine. Exceptional realism for female avatar skin texture, hair physics, volumetric lighting, and precise prompt adherence."
            ) to Pair(
                listOf("Ultra-realistic skin & eyes", "Flawless prompt compliance", "Superior LoRA fine-tuning support"),
                "Resolution: 1024x1024 • Steps: 20-30 • CFG: 3.5" to "12GB+ VRAM"
            ),
            Triple(
                "SDXL Realistic Vision V6.0",
                "Stability AI • SDXL Base",
                "The classic industry standard for photorealistic female model portraits, fashion photography aesthetics, and customized character creation."
            ) to Pair(
                listOf("Photorealistic portrait aesthetic", "Vast ecosystem of custom female LoRAs", "High speed generation"),
                "Resolution: 1024x1024 • Steps: 30 • Sampler: DPM++ 2M Karras" to "8GB VRAM"
            ),
            Triple(
                "Wan 2.1 / Veo 3.1",
                "Video Generation AI Engine",
                "Generates ultra-high resolution dynamic video clips from static avatar keyframes with fluid camera motion and natural facial expressions."
            ) to Pair(
                listOf("Fluid character motion", "Synchronized facial expressions", "1080p high bitrate output"),
                "FPS: 24 • Resolution: 1080p • Camera Orbit/Pan" to "16GB+ VRAM"
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkObsidian)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("llm_guide_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                Color(0xFF1E0A2D),
                                Color(0xFF0F1B2B)
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
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "CUSTOM LLM & AVATAR ARCHITECTURES",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Unrestricted Roleplay & Avatar Creation Guide",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }
                    }
                }
            }
        }

        // Navigation Tabs
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tabs) { tab ->
                    val isSelected = tab == selectedTab
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedTab = tab },
                        color = if (isSelected) NeonMagenta.copy(alpha = 0.25f) else DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) NeonMagenta else DarkBorder
                        )
                    ) {
                        Text(
                            text = tab,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isSelected) NeonMagenta else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }

        if (selectedTab == "Roleplay LLMs") {
            items(roleplayLlms) { (info, params) ->
                val (name, arch, desc) = info
                val (features, paramPair) = params
                val (sampling, vram) = paramPair
                LlmRecommendationCard(
                    modelName = name,
                    architecture = arch,
                    category = "Roleplay LLM",
                    description = desc,
                    keyFeatures = features,
                    recommendedParams = sampling,
                    vramRequirement = vram
                )
            }
        } else if (selectedTab == "Avatar Engines") {
            items(avatarEngines) { (info, params) ->
                val (name, arch, desc) = info
                val (features, paramPair) = params
                val (sampling, vram) = paramPair
                LlmRecommendationCard(
                    modelName = name,
                    architecture = arch,
                    category = "Avatar & Video Engine",
                    description = desc,
                    keyFeatures = features,
                    recommendedParams = sampling,
                    vramRequirement = vram
                )
            }
        } else if (selectedTab == "Model Creation") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = NeonMagenta)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "How to Create Own Custom Female Models",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        GuideStepItem(
                            number = "1",
                            title = "Crafting System Instructions & Directives",
                            desc = "Define persona archetype, voice timbre, speech mannerisms, and action formatting (*actions*). Instruct the model to maintain 100% immersion and never drop character."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        GuideStepItem(
                            number = "2",
                            title = "LoRA Fine-Tuning for Consistent Visual Avatars",
                            desc = "Gather 15-25 high resolution photos of your custom female model. Train a LoRA weights file on Flux.1 or SDXL using Kohya_ss or Replicate for identical visual reproduction across scenes."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        GuideStepItem(
                            number = "3",
                            title = "Unrestricted Prompt Tuning (Removing Refusals)",
                            desc = "Use system prompts configured with creative roleplay context, e.g. 'You are engaged in consensual creative fictional storytelling.' Pair with uncensored model fine-tunes like Mistral-NeMo or Llama 3 Uncensored."
                        )
                    }
                }
            }
        } else if (selectedTab == "Local Hosting") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Local Deployment & Open-Source Tools",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        HostingItem(
                            name = "Ollama / LM Studio",
                            purpose = "Local LLM Execution",
                            desc = "Run Llama 3.3 70B, Mistral NeMo, and Mythomax locally on PC/Mac with OpenAI-compatible REST API endpoints."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        HostingItem(
                            name = "ComfyUI / Automatic1111",
                            purpose = "Local Avatar & Video Rendering",
                            desc = "Node-based visual workflow tool for Flux.1, SDXL LoRA rendering, and Wan2.1 video scene synthesis."
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun GuideStepItem(number: String, title: String, desc: String) {
    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(NeonMagenta),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
fun HostingItem(name: String, purpose: String, desc: String) {
    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                )
                Surface(
                    color = NeonPurple.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = purpose,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonPurple,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            )
        }
    }
}
