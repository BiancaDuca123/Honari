package com.honari.app.presentation.screens.profile

import com.honari.app.domain.model.User

data class ProfileUiState(
    val user: User? = null,
    val totalRead: Int = 0,
    val wantToRead: Int = 0,
    val isDarkMode: Boolean = false,
)
