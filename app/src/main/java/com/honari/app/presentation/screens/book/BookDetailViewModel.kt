package com.honari.app.presentation.screens.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.repository.AuthRepository
import com.honari.app.domain.repository.BookLookupRepository
import com.honari.app.domain.repository.LibraryRepository
import com.honari.app.domain.usecase.AddBookToLibraryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Book Detail screen.
 * Manages book details and related operations.
 */
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookLookupRepository: BookLookupRepository,
    private val authRepository: AuthRepository,
    private val libraryRepository: LibraryRepository,
    private val addBookToLibrary: AddBookToLibraryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    /**
     * Loads book details by ID, then checks if it is already in the user's library.
     */
    fun loadBook(bookId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            bookLookupRepository.getBookById(bookId)
                .onSuccess { book ->
                    _uiState.update { it.copy(isLoading = false, book = book) }
                    checkIfInLibrary(bookId)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private suspend fun checkIfInLibrary(bookId: String) {
        val user = authRepository.getCurrentUser().first() ?: return
        val library = libraryRepository.getUserLibrary(user.id).first()
        val inLibrary = library.any { it.id == bookId }
        _uiState.update { it.copy(isInLibrary = inLibrary) }
    }

    /**
     * Adds book to library and only flips isInLibrary on confirmed success.
     */
    fun addToLibrary(status: ReadingStatus = ReadingStatus.WANT_TO_READ) {
        val book = _uiState.value.book ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val user = authRepository.getCurrentUser().first() ?: run {
                _uiState.update { it.copy(isLoading = false, error = "Not signed in") }
                return@launch
            }
            addBookToLibrary(user.id, book, status)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isInLibrary = true) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Failed to add to library")
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
