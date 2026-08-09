package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personaId: String,
    val sender: String, // "user" or "model"
    val text: String,
    val actionText: String? = null,
    val snapshotPrompt: String? = null,
    val photoPrompt: String? = null,
    val photoUrl: String? = null,
    val photoAspectRatio: String? = null,
    val photoStyle: String? = null,
    val videoPrompt: String? = null,
    val videoCameraMotion: String? = null,
    val videoDurationSec: Int? = null,
    val avatarExpression: String? = null,
    val avatarGesture: String? = null,
    val avatarVoiceEmotion: String? = null,
    val memoryUpdatesJson: String? = null,
    val rawPayloadJson: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
