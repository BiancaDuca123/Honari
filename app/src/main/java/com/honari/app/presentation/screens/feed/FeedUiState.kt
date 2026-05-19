package com.honari.app.presentation.screens.feed

import com.honari.app.domain.model.Book

data class FeedUiState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<Book> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null,
)
