package com.honari.app.presentation.screens.profile

import com.honari.app.domain.model.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val totalBooks: Int = 0,
    val currentlyReading: Int = 0,
    val booksRead: Int = 0,
    val wantToRead: Int = 0,
    val readingGoal: Int = 0,
    val error: String? = null
)
