package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personas")
data class PersonaEntity(
    @PrimaryKey val id: String,
    val name: String,
    val title: String,
    val age: Int = 24,
    val avatarCategory: String, // e.g. "Photorealistic", "Cyberpunk", "Anime 3D", "Noir", "Fantasy"
    val avatarColorHex: String,
    val avatarSymbol: String,
    val personality: String,
    val backstory: String = "",
    val scenario: String,
    val voiceStyle: String,
    val creativityTemp: Float,
    val systemPrompt: String,
    val avatarImageUrl: String = "",
    val avatarPrompt: String = "",
    val isCustom: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
