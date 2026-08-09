package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persona_memories")
data class PersonaMemoryEntity(
    @PrimaryKey val key: String,
    val value: String,
    val personaId: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
