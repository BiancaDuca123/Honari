package com.honari.app.domain.model

/**
 * UI state for the Book Detail screen.
 */
data class BookDetailUiState(
    val isLoading: Boolean = false,
    val book: Book? = null,
    val similarBooks: List<Book> = emptyList(),
    val isInLibrary: Boolean = false,
    val error: String? = null
)