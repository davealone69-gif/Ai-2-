package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_projects")
data class VideoProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val personaId: String,
    val prompt: String,
    val style: String,
    val scenesJson: String, // Stored as JSON string
    val createdAt: Long = System.currentTimeMillis()
)

data class VideoSceneItem(
    val sceneNumber: Int,
    val shotType: String, // e.g. "Extreme Close-Up", "Cinematic Wide", "Medium Tracking Shot"
    val visualPrompt: String,
    val narrationText: String,
    val cameraMotion: String, // "Slow Zoom In", "Orbit Right", "Static Dolly"
    val audioMood: String // "Dark Ambient Synth", "Cinematic Orchestral", "Cyberpunk Pulse"
)
