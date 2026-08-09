package com.example.ui.theme

import androidx.compose.ui.graphics.Color

data class ChatTheme(
    val id: String,
    val name: String,
    val description: String,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val topBarColor: Color,
    val userBubbleColor: Color,
    val userBubbleTextColor: Color,
    val userBubbleBorderColor: Color,
    val aiBubbleColor: Color,
    val aiBubbleTextColor: Color,
    val aiBubbleBorderColor: Color,
    val accentColor: Color,
    val actionTextColor: Color,
    val gradientBackground: List<Color>
)

object ChatThemePresets {
    val CyberpunkNeon = ChatTheme(
        id = "cyberpunk",
        name = "Cyberpunk Neon",
        description = "Futuristic neon magenta, purple & electric cyan vibes",
        backgroundColor = DarkObsidian,
        surfaceColor = DarkSurface,
        topBarColor = DarkSurface,
        userBubbleColor = NeonPurple.copy(alpha = 0.35f),
        userBubbleTextColor = TextPrimary,
        userBubbleBorderColor = NeonPurple.copy(alpha = 0.6f),
        aiBubbleColor = DarkSurfaceVariant,
        aiBubbleTextColor = TextPrimary,
        aiBubbleBorderColor = DarkBorder,
        accentColor = NeonMagenta,
        actionTextColor = NeonCyan,
        gradientBackground = listOf(DarkObsidian, Color(0xFF130920), DarkObsidian)
    )

    val SunsetRomance = ChatTheme(
        id = "sunset",
        name = "Sunset Romance",
        description = "Warm crimson, gold, and soft rose pink aesthetic",
        backgroundColor = Color(0xFF180A10),
        surfaceColor = Color(0xFF26121B),
        topBarColor = Color(0xFF26121B),
        userBubbleColor = Color(0xFF831843).copy(alpha = 0.6f),
        userBubbleTextColor = Color(0xFFFFF1F2),
        userBubbleBorderColor = Color(0xFFF43F5E),
        aiBubbleColor = Color(0xFF3B1525),
        aiBubbleTextColor = Color(0xFFFDE8E8),
        aiBubbleBorderColor = Color(0xFF9F1239),
        accentColor = Color(0xFFF43F5E),
        actionTextColor = Color(0xFFFBBF24),
        gradientBackground = listOf(Color(0xFF180A10), Color(0xFF2A0F1A), Color(0xFF180A10))
    )

    val GothicMidnight = ChatTheme(
        id = "gothic",
        name = "Gothic Midnight",
        description = "Dark velvet violet, deep obsidian, and silver mist",
        backgroundColor = Color(0xFF09070F),
        surfaceColor = Color(0xFF13101E),
        topBarColor = Color(0xFF13101E),
        userBubbleColor = Color(0xFF312E81).copy(alpha = 0.6f),
        userBubbleTextColor = Color(0xFFEEF2FF),
        userBubbleBorderColor = Color(0xFF6366F1),
        aiBubbleColor = Color(0xFF1E1B2E),
        aiBubbleTextColor = Color(0xFFE2E8F0),
        aiBubbleBorderColor = Color(0xFF475569),
        accentColor = Color(0xFF818CF8),
        actionTextColor = Color(0xFFA78BFA),
        gradientBackground = listOf(Color(0xFF09070F), Color(0xFF120E24), Color(0xFF09070F))
    )

    val EmeraldForest = ChatTheme(
        id = "emerald",
        name = "Emerald Forest",
        description = "Mystical dark emerald, jade glow, and mint highlights",
        backgroundColor = Color(0xFF04140C),
        surfaceColor = Color(0xFF0A2216),
        topBarColor = Color(0xFF0A2216),
        userBubbleColor = Color(0xFF065F46).copy(alpha = 0.6f),
        userBubbleTextColor = Color(0xFFECFDF5),
        userBubbleBorderColor = Color(0xFF10B981),
        aiBubbleColor = Color(0xFF113825),
        aiBubbleTextColor = Color(0xFFE6F4ED),
        aiBubbleBorderColor = Color(0xFF047857),
        accentColor = Color(0xFF10B981),
        actionTextColor = Color(0xFF34D399),
        gradientBackground = listOf(Color(0xFF04140C), Color(0xFF0C2B1C), Color(0xFF04140C))
    )

    val CelestialStarlight = ChatTheme(
        id = "celestial",
        name = "Celestial Starlight",
        description = "Deep cosmos indigo, electric blue, and star dust purple",
        backgroundColor = Color(0xFF070D1E),
        surfaceColor = Color(0xFF0F172A),
        topBarColor = Color(0xFF0F172A),
        userBubbleColor = Color(0xFF1E40AF).copy(alpha = 0.5f),
        userBubbleTextColor = Color(0xFFEFF6FF),
        userBubbleBorderColor = Color(0xFF3B82F6),
        aiBubbleColor = Color(0xFF1E293B),
        aiBubbleTextColor = Color(0xFFF1F5F9),
        aiBubbleBorderColor = Color(0xFF334155),
        accentColor = Color(0xFF38BDF8),
        actionTextColor = Color(0xFF60A5FA),
        gradientBackground = listOf(Color(0xFF070D1E), Color(0xFF101B38), Color(0xFF070D1E))
    )

    val CozyVintage = ChatTheme(
        id = "vintage",
        name = "Cozy Vintage",
        description = "Warm coffee sepia, terracotta bubbles, and amber highlights",
        backgroundColor = Color(0xFF18120C),
        surfaceColor = Color(0xFF241C15),
        topBarColor = Color(0xFF241C15),
        userBubbleColor = Color(0xFF78350F).copy(alpha = 0.6f),
        userBubbleTextColor = Color(0xFFFEF3C7),
        userBubbleBorderColor = Color(0xFFD97706),
        aiBubbleColor = Color(0xFF32261C),
        aiBubbleTextColor = Color(0xFFFDF8F0),
        aiBubbleBorderColor = Color(0xFF92400E),
        accentColor = Color(0xFFF59E0B),
        actionTextColor = Color(0xFFFBBF24),
        gradientBackground = listOf(Color(0xFF18120C), Color(0xFF2A1E14), Color(0xFF18120C))
    )

    val RoyalVelvet = ChatTheme(
        id = "royal",
        name = "Royal Velvet",
        description = "Opulent deep burgundy, imperial purple & gold radiance",
        backgroundColor = Color(0xFF1A081C),
        surfaceColor = Color(0xFF2B0F2E),
        topBarColor = Color(0xFF2B0F2E),
        userBubbleColor = Color(0xFF581C87).copy(alpha = 0.6f),
        userBubbleTextColor = Color(0xFFFAF5FF),
        userBubbleBorderColor = Color(0xFFA855F7),
        aiBubbleColor = Color(0xFF3B1238),
        aiBubbleTextColor = Color(0xFFFDF4FF),
        aiBubbleBorderColor = Color(0xFF86198F),
        accentColor = Color(0xFFF59E0B),
        actionTextColor = Color(0xFFFCD34D),
        gradientBackground = listOf(Color(0xFF1A081C), Color(0xFF2D0E30), Color(0xFF1A081C))
    )

    val MatrixGreen = ChatTheme(
        id = "matrix",
        name = "Matrix Code",
        description = "Monochrome dark terminal with phosphor green highlights",
        backgroundColor = Color(0xFF050B06),
        surfaceColor = Color(0xFF0A180E),
        topBarColor = Color(0xFF0A180E),
        userBubbleColor = Color(0xFF14532D).copy(alpha = 0.6f),
        userBubbleTextColor = Color(0xFFDCFCE7),
        userBubbleBorderColor = Color(0xFF22C55E),
        aiBubbleColor = Color(0xFF0D2818),
        aiBubbleTextColor = Color(0xFFF0FDF4),
        aiBubbleBorderColor = Color(0xFF15803D),
        accentColor = Color(0xFF22C55E),
        actionTextColor = Color(0xFF4ADE80),
        gradientBackground = listOf(Color(0xFF050B06), Color(0xFF0B1F10), Color(0xFF050B06))
    )

    val allThemes = listOf(
        CyberpunkNeon,
        SunsetRomance,
        GothicMidnight,
        EmeraldForest,
        CelestialStarlight,
        CozyVintage,
        RoyalVelvet,
        MatrixGreen
    )

    fun getThemeById(id: String): ChatTheme {
        return allThemes.find { it.id == id } ?: CyberpunkNeon
    }

    fun getDefaultThemeForCategory(category: String): ChatTheme {
        return when (category.lowercase()) {
            "photorealistic", "romance" -> SunsetRomance
            "cyberpunk", "sci-fi" -> CyberpunkNeon
            "noir", "gothic" -> GothicMidnight
            "fantasy", "anime 3d" -> EmeraldForest
            "historical", "vintage" -> CozyVintage
            "royal", "drama" -> RoyalVelvet
            "matrix", "hacker" -> MatrixGreen
            else -> CyberpunkNeon
        }
    }
}
