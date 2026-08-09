package com.example.ui

import androidx.compose.runtime.Immutable

/**
 * Enterprise Sealed Interface representing Unidirectional Data Flow (UDF) UI state.
 */
@Immutable
sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(
        val message: String,
        val exception: Throwable? = null,
        val isApiKeyError: Boolean = false,
        val isQuotaError: Boolean = false,
        val canRetry: Boolean = true
    ) : UiState<Nothing>
}
