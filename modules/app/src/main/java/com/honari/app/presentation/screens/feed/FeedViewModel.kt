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

private const val DEFAULT_QUERY = "subject:fiction"
private const val SEARCH_DEBOUNCE_MS = 500L
private const val MAX_SUBJECTS = 3
private const val MAX_AUTHORS = 2
private const val MAX_TOP_PICKS = 10

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
                bookRepository.getFeedBooks(query = DEFAULT_QUERY).collect { books ->
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
                val query = buildRecommendationQuery()
                val picks = bookRepository.searchBooks(query, maxResults = MAX_TOP_PICKS)
                _uiState.update { it.copy(topPicksBooks = picks) }
            }
        }
    }

    private suspend fun buildRecommendationQuery(): String {
        val libraryBooks = libraryRepository.getAllBooks().firstOrNull() ?: emptyList()
        if (libraryBooks.isEmpty()) return DEFAULT_QUERY

        val subjects = libraryBooks.flatMap { it.categories }
            .filter { it.isNotBlank() }
            .distinct()
            .take(MAX_SUBJECTS)
        val authors = libraryBooks.flatMap { it.authors }
            .filter { it.isNotBlank() }
            .distinct()
            .take(MAX_AUTHORS)

        if (subjects.isEmpty() && authors.isEmpty()) return DEFAULT_QUERY

        val parts = mutableListOf<String>()
        subjects.forEach { parts.add("subject:${formatRecommendationTerm(it)}") }
        authors.forEach { parts.add("inauthor:${formatRecommendationTerm(it)}") }
        return parts.joinToString("+")
    }

    private fun formatRecommendationTerm(value: String): String =
        value.lowercase().replace(" ", "+")

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
        _uiState.update { it.copy(selectedGenre = if (it.selectedGenre == genre) null else genre) }
    }
}
