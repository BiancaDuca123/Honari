package com.honari.app.presentation.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LibraryFilter(val title: String) {
    CONTINUE_READING("Continue Reading"),
    WISH_LIST("My Wish List"),
    ALL_BOOKS("All Books"),
}

data class LibraryUiState(
    val isLoading: Boolean = false,
    val allBooks: List<Book> = emptyList(),
    val selectedFilter: LibraryFilter? = null,
    val error: String? = null,
) {
    val displayedBooks: List<Book>
        get() = when (selectedFilter) {
            LibraryFilter.CONTINUE_READING -> allBooks.filter {
                it.libraryStatus == ReadingStatus.READ
            }
            LibraryFilter.WISH_LIST -> allBooks.filter {
                it.libraryStatus == ReadingStatus.WANT_TO_READ
            }
            LibraryFilter.ALL_BOOKS -> allBooks
            null -> emptyList()
        }
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedFilter = LibraryFilter.ALL_BOOKS) }
            libraryRepository.getAllBooks().collect { books ->
                _uiState.update { it.copy(isLoading = false, allBooks = books) }
            }
        }
    }

    fun selectFilter(filter: LibraryFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun removeBook(bookId: String) {
        viewModelScope.launch {
            runCatching { libraryRepository.removeBook(bookId) }
                .onFailure { throwable ->
                    _uiState.update { state ->
                        state.copy(error = throwable.message ?: "Failed to remove book")
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
