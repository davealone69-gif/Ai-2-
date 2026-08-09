package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class AvatarTraits(
    val name: String = "Aura",
    val hairStyle: String = "Long Waves",
    val hairColor: String = "Neon Pink",
    val eyeStyle: String = "Anime Sparkle",
    val eyeColor: String = "Sapphire Blue",
    val clothingStyle: String = "Cyber Jacket",
    val clothingColor: String = "Royal Purple",
    val skinTone: String = "Porcelain",
    val expression: String = "Confident Smile",
    val accessory: String = "Cyber Visor",
    val backgroundStyle: String = "Neon Grid",
    val personality: String = "Cheerful"
)

data class AppPreferences(
    val username: String = "Creator",
    val email: String = "user@aistudio.dev",
    val isDarkMode: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val aiTemperature: Float = 0.7f,
    val aiModelName: String = "gemini-1.5-flash",
    val topP: Float = 0.95f,
    val maxOutputTokens: Int = 2048,
    val themePreference: String = "Dark Obsidian",
    val autoThemeEnabled: Boolean = true,
    val ttsAutoPlay: Boolean = false
)

data class UserProfileSettings(
    val username: String = "",
    val email: String = "",
    val isDarkMode: Boolean = true,
    val notificationsEnabled: Boolean = true
)

data class ApplicationState(
    val isFirstLaunch: Boolean = true,
    val lastActiveTab: Int = 0,
    val lastSyncTimestamp: Long = 0L
)

class DataStoreManager(private val context: Context) {

    private object PreferencesKeys {
        val USERNAME = stringPreferencesKey("username")
        val EMAIL = stringPreferencesKey("email")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")

        val AI_TEMPERATURE = floatPreferencesKey("ai_temperature")
        val AI_MODEL_NAME = stringPreferencesKey("ai_model_name")
        val TOP_P = floatPreferencesKey("top_p")
        val MAX_OUTPUT_TOKENS = intPreferencesKey("max_output_tokens")
        val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
        val AUTO_THEME_ENABLED = booleanPreferencesKey("auto_theme_enabled")
        val TTS_AUTO_PLAY = booleanPreferencesKey("tts_auto_play")

        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val LAST_ACTIVE_TAB = intPreferencesKey("last_active_tab")
        val LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")

        val AVATAR_NAME = stringPreferencesKey("avatar_name")
        val AVATAR_HAIR_STYLE = stringPreferencesKey("avatar_hair_style")
        val AVATAR_HAIR_COLOR = stringPreferencesKey("avatar_hair_color")
        val AVATAR_EYE_STYLE = stringPreferencesKey("avatar_eye_style")
        val AVATAR_EYE_COLOR = stringPreferencesKey("avatar_eye_color")
        val AVATAR_CLOTHING_STYLE = stringPreferencesKey("avatar_clothing_style")
        val AVATAR_CLOTHING_COLOR = stringPreferencesKey("avatar_clothing_color")
        val AVATAR_SKIN_TONE = stringPreferencesKey("avatar_skin_tone")
        val AVATAR_EXPRESSION = stringPreferencesKey("avatar_expression")
        val AVATAR_ACCESSORY = stringPreferencesKey("avatar_accessory")
        val AVATAR_BACKGROUND = stringPreferencesKey("avatar_background")
        val AVATAR_PERSONALITY = stringPreferencesKey("avatar_personality")
    }

    val avatarTraitsFlow: Flow<AvatarTraits> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AvatarTraits(
                name = preferences[PreferencesKeys.AVATAR_NAME] ?: "Aura",
                hairStyle = preferences[PreferencesKeys.AVATAR_HAIR_STYLE] ?: "Long Waves",
                hairColor = preferences[PreferencesKeys.AVATAR_HAIR_COLOR] ?: "Neon Pink",
                eyeStyle = preferences[PreferencesKeys.AVATAR_EYE_STYLE] ?: "Anime Sparkle",
                eyeColor = preferences[PreferencesKeys.AVATAR_EYE_COLOR] ?: "Sapphire Blue",
                clothingStyle = preferences[PreferencesKeys.AVATAR_CLOTHING_STYLE] ?: "Cyber Jacket",
                clothingColor = preferences[PreferencesKeys.AVATAR_CLOTHING_COLOR] ?: "Royal Purple",
                skinTone = preferences[PreferencesKeys.AVATAR_SKIN_TONE] ?: "Porcelain",
                expression = preferences[PreferencesKeys.AVATAR_EXPRESSION] ?: "Confident Smile",
                accessory = preferences[PreferencesKeys.AVATAR_ACCESSORY] ?: "Cyber Visor",
                backgroundStyle = preferences[PreferencesKeys.AVATAR_BACKGROUND] ?: "Neon Grid",
                personality = preferences[PreferencesKeys.AVATAR_PERSONALITY] ?: "Cheerful"
            )
        }

    val appPreferencesFlow: Flow<AppPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AppPreferences(
                username = preferences[PreferencesKeys.USERNAME] ?: "Creator",
                email = preferences[PreferencesKeys.EMAIL] ?: "user@aistudio.dev",
                isDarkMode = preferences[PreferencesKeys.IS_DARK_MODE] ?: true,
                notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
                aiTemperature = preferences[PreferencesKeys.AI_TEMPERATURE] ?: 0.7f,
                aiModelName = preferences[PreferencesKeys.AI_MODEL_NAME] ?: "gemini-1.5-flash",
                topP = preferences[PreferencesKeys.TOP_P] ?: 0.95f,
                maxOutputTokens = preferences[PreferencesKeys.MAX_OUTPUT_TOKENS] ?: 2048,
                themePreference = preferences[PreferencesKeys.THEME_PREFERENCE] ?: "Dark Obsidian",
                autoThemeEnabled = preferences[PreferencesKeys.AUTO_THEME_ENABLED] ?: true,
                ttsAutoPlay = preferences[PreferencesKeys.TTS_AUTO_PLAY] ?: false
            )
        }

    val userProfileSettingsFlow: Flow<UserProfileSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserProfileSettings(
                username = preferences[PreferencesKeys.USERNAME] ?: "",
                email = preferences[PreferencesKeys.EMAIL] ?: "",
                isDarkMode = preferences[PreferencesKeys.IS_DARK_MODE] ?: true,
                notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
            )
        }

    val applicationStateFlow: Flow<ApplicationState> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            ApplicationState(
                isFirstLaunch = preferences[PreferencesKeys.IS_FIRST_LAUNCH] ?: true,
                lastActiveTab = preferences[PreferencesKeys.LAST_ACTIVE_TAB] ?: 0,
                lastSyncTimestamp = preferences[PreferencesKeys.LAST_SYNC_TIMESTAMP] ?: 0L
            )
        }

    suspend fun updateAiTemperature(temperature: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AI_TEMPERATURE] = temperature
        }
    }

    suspend fun updateAiModelName(modelName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AI_MODEL_NAME] = modelName
        }
    }

    suspend fun updateTopP(topP: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOP_P] = topP
        }
    }

    suspend fun updateMaxOutputTokens(tokens: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MAX_OUTPUT_TOKENS] = tokens
        }
    }

    suspend fun updateThemePreference(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_PREFERENCE] = theme
        }
    }

    suspend fun updateAutoThemeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_THEME_ENABLED] = enabled
        }
    }

    suspend fun updateTtsAutoPlay(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TTS_AUTO_PLAY] = enabled
        }
    }

    suspend fun updateUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USERNAME] = username
        }
    }

    suspend fun updateEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EMAIL] = email
        }
    }

    suspend fun updateDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_MODE] = isDarkMode
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateUserProfile(username: String, email: String, isDarkMode: Boolean, notificationsEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USERNAME] = username
            preferences[PreferencesKeys.EMAIL] = email
            preferences[PreferencesKeys.IS_DARK_MODE] = isDarkMode
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = notificationsEnabled
        }
    }

    suspend fun setFirstLaunchCompleted() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_FIRST_LAUNCH] = false
        }
    }

    suspend fun updateLastActiveTab(tabIndex: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_ACTIVE_TAB] = tabIndex
        }
    }

    suspend fun updateLastSyncTimestamp(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SYNC_TIMESTAMP] = timestamp
        }
    }

    suspend fun updateAvatarTraits(traits: AvatarTraits) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AVATAR_NAME] = traits.name
            preferences[PreferencesKeys.AVATAR_HAIR_STYLE] = traits.hairStyle
            preferences[PreferencesKeys.AVATAR_HAIR_COLOR] = traits.hairColor
            preferences[PreferencesKeys.AVATAR_EYE_STYLE] = traits.eyeStyle
            preferences[PreferencesKeys.AVATAR_EYE_COLOR] = traits.eyeColor
            preferences[PreferencesKeys.AVATAR_CLOTHING_STYLE] = traits.clothingStyle
            preferences[PreferencesKeys.AVATAR_CLOTHING_COLOR] = traits.clothingColor
            preferences[PreferencesKeys.AVATAR_SKIN_TONE] = traits.skinTone
            preferences[PreferencesKeys.AVATAR_EXPRESSION] = traits.expression
            preferences[PreferencesKeys.AVATAR_ACCESSORY] = traits.accessory
            preferences[PreferencesKeys.AVATAR_BACKGROUND] = traits.backgroundStyle
            preferences[PreferencesKeys.AVATAR_PERSONALITY] = traits.personality
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
