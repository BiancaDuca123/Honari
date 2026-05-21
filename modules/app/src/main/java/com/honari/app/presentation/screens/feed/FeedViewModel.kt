package com.honari.app.presentation.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.Book
import com.honari.app.domain.repository.BookRepository
import com.honari.app.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SEARCH_DEBOUNCE_MS = 500L
private const val MAX_SUBJECTS = 3
private const val MAX_TOP_PICKS = 8

data class FeedUiState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val topPicksBooks: List<Book> = emptyList(),
    val selectedGenre: String? = null,
    val searchQuery: String = "",
    val searchResults: List<Book> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val libraryRepository: LibraryRepository,
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
            runCatching {
                // query param ignored by getFeedBooks — it always fetches popular RO books
                bookRepository.getFeedBooks(query = "").collect { books ->
                    _uiState.update { it.copy(isLoading = false, books = books) }
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(isLoading = false, error = throwable.message ?: "Failed to load books")
                }
            }
            loadTopPicks()
        }
    }

    private fun loadTopPicks() {
        viewModelScope.launch {
            runCatching {
                val libraryBooks = libraryRepository.getAllBooks().firstOrNull() ?: emptyList()
                val picks = if (libraryBooks.isEmpty()) {
                    bookRepository.searchBooks("subject:bestseller", maxResults = MAX_TOP_PICKS)
                } else {
                    val topGenres = libraryBooks.flatMap { it.categories }
                        .filter { it.isNotBlank() }
                        .groupingBy { it.lowercase().trim() }
                        .eachCount()
                        .entries.sortedByDescending { it.value }
                        .take(MAX_SUBJECTS)
                        .map { it.key }
                    topGenres.flatMap { genre ->
                        bookRepository.searchBooks("subject:$genre", maxResults = 8)
                    }.distinctBy { it.id }
                }
                _uiState.update { it.copy(topPicksBooks = picks) }
            }
        }
    }

    fun refreshFeed() {
        loadFeed()
    }

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
                        state.copy(
                            isSearching = false,
                            error = throwable.message ?: "Unable to search books",
                        )
                    }
                }
                .getOrDefault(emptyList())
            _uiState.update { it.copy(searchResults = results, isSearching = false) }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _uiState.update {
            it.copy(searchQuery = "", searchResults = emptyList(), isSearching = false)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onGenreSelected(genre: String) {
        val newGenre = if (_uiState.value.selectedGenre == genre) null else genre
        _uiState.update { it.copy(selectedGenre = newGenre) }
        feedJob?.cancel()
        feedJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val query = if (newGenre != null) "bestseller ${newGenre.lowercase()}" else ""
            runCatching {
                bookRepository.getFeedBooks(query = query).collect { books ->
                    _uiState.update { it.copy(isLoading = false, books = books) }
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(isLoading = false, error = throwable.message ?: "Failed to load books")
                }
            }
        }
    }
}
