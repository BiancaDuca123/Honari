package com.honari.app.domain.model

/**
 * UI state for authentication screens.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null,
    val passwordResetSent: Boolean = false
)
