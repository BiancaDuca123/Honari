package com.honari.app.domain.model

/**
 * UI state for Library screen.
 */
data class LibraryUiState(
    val isLoading: Boolean = false,
    val allBooks: List<LibraryBook> = emptyList(),
    val currentlyReading: List<LibraryBook> = emptyList(),
    val finishedBooks: List<LibraryBook> = emptyList(),
    val wantToRead: List<LibraryBook> = emptyList(),
    val stats: LibraryStats = LibraryStats(),
    val tabCounts: Map<LibraryTab, Int> = emptyMap(),
    val error: String? = null
)
