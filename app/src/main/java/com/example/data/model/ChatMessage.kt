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
    val timestamp: Long = System.currentTimeMillis()
)
