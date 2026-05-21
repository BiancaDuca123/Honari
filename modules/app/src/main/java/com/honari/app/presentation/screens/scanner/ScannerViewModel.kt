package com.honari.app.presentation.screens.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.repository.BookRepository
import com.honari.app.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val BOOK_NOT_FOUND_MESSAGE = "Book not found for this barcode"
private const val SAVE_ERROR_MESSAGE = "We couldn't save this book right now."
private const val READS_MESSAGE = "Added to your reads!"
private const val WISHLIST_MESSAGE = "Added to your wishlist!"

data class ScannerUiState(
    val isScanning: Boolean = true,
    val isLoading: Boolean = false,
    val scannedIsbn: String? = null,
    val scannedBook: Book? = null,
    val isInLibrary: Boolean = false,
    val error: String? = null,
    val addedMessage: String? = null,
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private var libraryObserverJob: Job? = null

    fun onBarcodeScanned(isbn: String) {
        if (_uiState.value.isLoading || _uiState.value.scannedIsbn == isbn) {
            return
        }
        libraryObserverJob?.cancel()
        _uiState.update {
            it.copy(
                isLoading = true,
                scannedIsbn = isbn,
                scannedBook = null,
                isScanning = false,
                isInLibrary = false,
                error = null,
                addedMessage = null,
            )
        }

        viewModelScope.launch {
            val book = bookRepository.getBookByIsbn(isbn)
            if (book != null) {
                _uiState.update { it.copy(isLoading = false, scannedBook = book) }
                libraryObserverJob = viewModelScope.launch {
                    libraryRepository.isBookInLibrary(book.id).collect { inLibrary ->
                        _uiState.update { currentState ->
                            currentState.copy(isInLibrary = inLibrary)
                        }
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        scannedIsbn = null,
                        isScanning = true,
                        error = BOOK_NOT_FOUND_MESSAGE,
                    )
                }
            }
        }
    }

    fun addToLibrary(status: ReadingStatus) {
        val scannedBook = _uiState.value.scannedBook ?: return
        val savedBook = scannedBook.copy(
            libraryStatus = status,
            addedAt = System.currentTimeMillis(),
        )
        viewModelScope.launch {
            runCatching {
                libraryRepository.addBook(savedBook)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        scannedBook = savedBook,
                        isInLibrary = true,
                        addedMessage = buildAddedMessage(status),
                        error = null,
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(error = SAVE_ERROR_MESSAGE) }
            }
        }
    }

    fun dismissSheet() {
        libraryObserverJob?.cancel()
        _uiState.update {
            it.copy(
                isScanning = true,
                isLoading = false,
                scannedIsbn = null,
                scannedBook = null,
                isInLibrary = false,
                error = null,
                addedMessage = null,
            )
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(
                error = null,
                isScanning = true,
                scannedIsbn = null,
                isLoading = false,
            )
        }
    }

    private fun buildAddedMessage(status: ReadingStatus): String =
        if (status == ReadingStatus.READ) {
            READS_MESSAGE
        } else {
            WISHLIST_MESSAGE
        }
}
