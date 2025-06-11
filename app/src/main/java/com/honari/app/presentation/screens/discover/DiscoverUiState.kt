package com.honari.app.presentation.screens.discover

import com.honari.app.domain.model.Book

data class DiscoverUiState(
    val isLoading: Boolean = false,
    val popularBooks: List<Book> = emptyList(),
    val newReleases: List<Book> = emptyList(),
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<Book> = emptyList(),
    val error: String? = null
)
