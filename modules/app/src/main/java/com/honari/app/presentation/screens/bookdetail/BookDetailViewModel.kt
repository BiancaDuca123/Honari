package com.honari.app.presentation.screens.bookdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.model.Review
import com.honari.app.domain.repository.BookRepository
import com.honari.app.domain.repository.LibraryRepository
import com.honari.app.domain.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val BOOK_ID_ARG = "bookId"
private const val SAVE_ERROR_MESSAGE = "We couldn't save this book right now."
private const val LOAD_ERROR_MESSAGE = "We couldn't load this book right now."

data class BookDetailUiState(
    val book: Book? = null,
    val reviews: List<Review> = emptyList(),
    val isInLibrary: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookRepository: BookRepository,
    private val reviewRepository: ReviewRepository,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle[BOOK_ID_ARG])

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    init {
        observeLibraryState()
        observeReviews()
        loadBook()
    }

    fun saveBook(status: ReadingStatus) {
        val currentBook = _uiState.value.book ?: return
        viewModelScope.launch {
            runCatching {
                if (_uiState.value.isInLibrary) {
                    libraryRepository.updateStatus(bookId, status)
                } else {
                    libraryRepository.addBook(
                        currentBook.copy(
                            libraryStatus = status,
                            addedAt = System.currentTimeMillis(),
                        ),
                    )
                }
            }.onSuccess {
                _uiState.update { it.copy(isInLibrary = true, error = null) }
            }.onFailure {
                _uiState.update { it.copy(error = SAVE_ERROR_MESSAGE) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun loadBook() {
        viewModelScope.launch {
            val book = runCatching {
                libraryRepository.getBookById(bookId) ?: bookRepository.getBookById(bookId)
            }.getOrNull()
            _uiState.update {
                it.copy(
                    book = book,
                    isLoading = false,
                    error = if (book == null) LOAD_ERROR_MESSAGE else null,
                )
            }
        }
    }

    private fun observeLibraryState() {
        viewModelScope.launch {
            libraryRepository.isBookInLibrary(bookId).collect { isInLibrary ->
                _uiState.update { it.copy(isInLibrary = isInLibrary) }
            }
        }
    }

    private fun observeReviews() {
        viewModelScope.launch {
            reviewRepository.getReviewsForBook(bookId).collect { reviews ->
                _uiState.update { it.copy(reviews = reviews, isLoading = false) }
            }
        }
    }
}
