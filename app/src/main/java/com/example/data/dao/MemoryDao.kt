package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PersonaMemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMemory(memory: PersonaMemoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMemories(memories: List<PersonaMemoryEntity>)

    @Query("SELECT * FROM persona_memories WHERE `key` = :key LIMIT 1")
    suspend fun getMemoryByKey(key: String): PersonaMemoryEntity?

    @Query("SELECT * FROM persona_memories ORDER BY updatedAt DESC")
    fun getAllMemoriesFlow(): Flow<List<PersonaMemoryEntity>>

    @Query("SELECT * FROM persona_memories ORDER BY updatedAt DESC")
    suspend fun getAllMemories(): List<PersonaMemoryEntity>

    @Query("DELETE FROM persona_memories")
    suspend fun clearAllMemories()
}
