package com.honari.app.presentation.screens.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.repository.BookLookupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(private val booksRepository: BookLookupRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    init {
        loadContent()
    }

    fun loadContent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val popularResult = booksRepository.getPopularBooks()
            val newReleasesResult = booksRepository.getNewReleases()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    popularBooks = popularResult.getOrElse { emptyList() },
                    newReleases = newReleasesResult.getOrElse { emptyList() },
                    error = when {
                        popularResult.isFailure && newReleasesResult.isFailure ->
                            "Failed to load books. Check your connection."
                        popularResult.isFailure ->
                            popularResult.exceptionOrNull()?.message
                        else -> null
                    }
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            _uiState.update { it.copy(isSearching = false, searchResults = emptyList()) }
        }
    }

    fun search() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isSearching = true, error = null) }
            val results = booksRepository.searchByQuery(query).getOrElse { emptyList() }
            _uiState.update { it.copy(isLoading = false, searchResults = results) }
        }
    }

    fun clearSearch() {
        _uiState.update {
            it.copy(searchQuery = "", isSearching = false, searchResults = emptyList())
        }
    }
}
