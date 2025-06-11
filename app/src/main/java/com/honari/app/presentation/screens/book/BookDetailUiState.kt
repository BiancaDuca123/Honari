package com.honari.app.presentation.screens.book

import com.honari.app.domain.model.Book

data class BookDetailUiState(
    val isLoading: Boolean = false,
    val book: Book? = null,
    val isInLibrary: Boolean = false,
    val error: String? = null
)
