package com.honari.app.presentation.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SEARCH_DEBOUNCE_MS = 500L

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val bookRepository: BookRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var feedJob: Job? = null

    init {
        loadFeed()
    }

    fun loadFeed() {
        feedJob?.cancel()
        feedJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            bookRepository.getFeedBooks().collect { books ->
                _uiState.update { it.copy(isLoading = false, books = books) }
            }
        }
    }

    fun refreshFeed() = loadFeed()

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, error = null) }
            delay(SEARCH_DEBOUNCE_MS)
            val results = runCatching { bookRepository.searchBooks(query) }
                .onFailure { throwable ->
                    _uiState.update { state ->
                        state.copy(isSearching = false, error = throwable.message ?: "Unable to search books")
                    }
                }
                .getOrDefault(emptyList())
            _uiState.update { it.copy(searchResults = results, isSearching = false) }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _uiState.update { it.copy(searchQuery = "", searchResults = emptyList(), isSearching = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
