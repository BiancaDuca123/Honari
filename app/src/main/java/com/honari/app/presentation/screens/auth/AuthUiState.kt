package com.honari.app.presentation.screens.auth

import com.honari.app.domain.model.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null,
)
