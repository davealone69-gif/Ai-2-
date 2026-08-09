package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ChatDao
import com.example.data.dao.PersonaDao
import com.example.data.dao.VideoDao
import com.example.data.model.ChatMessageEntity
import com.example.data.model.PersonaEntity
import com.example.data.model.VideoProjectEntity

@Database(
    entities = [
        PersonaEntity::class,
        ChatMessageEntity::class,
        VideoProjectEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AuraDatabase : RoomDatabase() {
    abstract fun personaDao(): PersonaDao
    abstract fun chatDao(): ChatDao
    abstract fun videoDao(): VideoDao

    companion object {
        @Volatile
        private var INSTANCE: AuraDatabase? = null

        fun getDatabase(context: Context): AuraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AuraDatabase::class.java,
                    "aura_studio_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
