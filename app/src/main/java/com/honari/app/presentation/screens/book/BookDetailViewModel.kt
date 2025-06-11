package com.honari.app.presentation.screens.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.BookDetailUiState
import com.honari.app.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Book Detail screen.
 * Manages book details and related operations.
 */
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    /**
     * Loads book details by ID.
     */
    fun loadBook(bookId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val book = bookRepository.getBookById(bookId)

            if (book != null) {
                _uiState.value = _uiState.value.copy(
                    book = book,
                    isLoading = false
                )
                loadSimilarBooks(book.mood)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Book not found"
                )
            }
        }
    }

    /**
     * Loads similar books based on mood.
     */
    private fun loadSimilarBooks(mood: String) {
        viewModelScope.launch {
            bookRepository.getBooksByMood(mood).collect { books ->
                _uiState.value = _uiState.value.copy(
                    similarBooks = books.filter { it.id != _uiState.value.book?.id }
                )
            }
        }
    }

    /**
     * Toggles book in library.
     */
    fun toggleLibrary() {
        _uiState.value = _uiState.value.copy(
            isInLibrary = !_uiState.value.isInLibrary
        )
        // TODO: Implement actual library addition/removal
    }
}
