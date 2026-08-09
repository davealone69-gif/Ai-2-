package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppPreferences
import com.example.data.AvatarTraits
import com.example.data.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    val appPreferences: StateFlow<AppPreferences> = dataStoreManager.appPreferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppPreferences()
    )

    val avatarTraits: StateFlow<AvatarTraits> = dataStoreManager.avatarTraitsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AvatarTraits()
    )

    fun updateAvatarTraits(traits: AvatarTraits) {
        viewModelScope.launch {
            dataStoreManager.updateAvatarTraits(traits)
        }
    }

    fun updateAiTemperature(temperature: Float) {
        viewModelScope.launch {
            dataStoreManager.updateAiTemperature(temperature)
        }
    }

    fun updateAiModelName(modelName: String) {
        viewModelScope.launch {
            dataStoreManager.updateAiModelName(modelName)
        }
    }

    fun updateTopP(topP: Float) {
        viewModelScope.launch {
            dataStoreManager.updateTopP(topP)
        }
    }

    fun updateMaxOutputTokens(tokens: Int) {
        viewModelScope.launch {
            dataStoreManager.updateMaxOutputTokens(tokens)
        }
    }

    fun updateThemePreference(theme: String) {
        viewModelScope.launch {
            dataStoreManager.updateThemePreference(theme)
        }
    }

    fun updateDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            dataStoreManager.updateDarkMode(isDarkMode)
        }
    }

    fun updateAutoThemeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.updateAutoThemeEnabled(enabled)
        }
    }

    fun updateTtsAutoPlay(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.updateTtsAutoPlay(enabled)
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.updateNotificationsEnabled(enabled)
        }
    }

    fun updateUserProfile(username: String, email: String) {
        viewModelScope.launch {
            dataStoreManager.updateUsername(username)
            dataStoreManager.updateEmail(email)
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            dataStoreManager.clearAll()
        }
    }
}
