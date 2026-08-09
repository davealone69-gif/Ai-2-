package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.GeminiRepository
import com.example.ui.UiState
import com.google.ai.client.generativeai.type.GoogleGenerativeAIException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Enterprise ViewModel managing UDF architecture with StateFlow & Coroutines.
 */
class MainViewModel(
    private val repository: GeminiRepository = GeminiRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uiState: StateFlow<UiState<String>> = _uiState.asStateFlow()

    private var lastPrompt: String? = null
    private var lastModelName: String = "gemini-1.5-flash"
    private var lastSystemInstruction: String? = null

    /**
     * Executes content generation asynchronously using Coroutines & StateFlow.
     */
    fun generateContent(
        prompt: String,
        modelName: String = "gemini-1.5-flash",
        systemInstruction: String? = null
    ) {
        if (prompt.isBlank()) return

        lastPrompt = prompt
        lastModelName = modelName
        lastSystemInstruction = systemInstruction

        _uiState.value = UiState.Loading

        viewModelScope.launch {
            val result = repository.generateContent(
                prompt = prompt,
                modelName = modelName,
                systemInstruction = systemInstruction
            )

            result.fold(
                onSuccess = { responseText ->
                    _uiState.value = UiState.Success(responseText)
                },
                onFailure = { throwable ->
                    _uiState.value = mapThrowableToErrorState(throwable)
                }
            )
        }
    }

    /**
     * Retries the previous execution attempt seamlessly.
     */
    fun retry() {
        lastPrompt?.let { prompt ->
            generateContent(
                prompt = prompt,
                modelName = lastModelName,
                systemInstruction = lastSystemInstruction
            )
        }
    }

    /**
     * Resets state back to Idle.
     */
    fun resetState() {
        _uiState.value = UiState.Idle
    }

    private fun mapThrowableToErrorState(throwable: Throwable): UiState.Error {
        val message = throwable.message ?: "An unexpected error occurred during API execution."
        val isKeyError = throwable is IllegalArgumentException || message.contains("API key", ignoreCase = true)
        val isQuota = message.contains("429", ignoreCase = true) || message.contains("quota", ignoreCase = true)

        val userFriendlyMessage = when {
            isKeyError -> "Invalid or missing API Key. Please verify your GEMINI_API_KEY in the Secrets panel."
            isQuota -> "Gemini API rate limit reached. Please wait a moment and try again."
            message.contains("ConnectException", ignoreCase = true) || message.contains("UnknownHostException", ignoreCase = true) ->
                "Network connection lost. Check your Internet connection and retry."
            else -> "Execution Failed: $message"
        }

        return UiState.Error(
            message = userFriendlyMessage,
            exception = throwable,
            isApiKeyError = isKeyError,
            isQuotaError = isQuota,
            canRetry = !isKeyError
        )
    }
}
