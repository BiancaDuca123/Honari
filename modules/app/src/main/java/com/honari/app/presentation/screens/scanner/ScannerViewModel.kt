package com.honari.app.presentation.screens.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.repository.BookRepository
import com.honari.app.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val BOOK_NOT_FOUND_MESSAGE = "Book not found for this barcode"
private const val SAVE_ERROR_MESSAGE = "We couldn't save this book right now."
private const val SEARCH_FAILED_MESSAGE = "Search failed"
private const val READS_MESSAGE = "Added to your reads!"
private const val WISHLIST_MESSAGE = "Added to your wishlist!"
private const val MIN_TEXT_LENGTH = 5
private const val TEXT_SEARCH_DEBOUNCE_MS = 1_500L

enum class ScanMode { BARCODE, COVER }

data class ScannerUiState(
    val isScanning: Boolean = true,
    val isLoading: Boolean = false,
    val scannedIsbn: String? = null,
    val scannedBook: Book? = null,
    val isInLibrary: Boolean = false,
    val error: String? = null,
    val addedMessage: String? = null,
    val scanMode: ScanMode = ScanMode.BARCODE,
    val detectedText: String = "",
    val isSearchingByText: Boolean = false,
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private var libraryObserverJob: Job? = null
    private var textSearchJob: Job? = null

    fun switchMode(mode: ScanMode) {
        if (_uiState.value.scanMode == mode) {
            return
        }
        textSearchJob?.cancel()
        libraryObserverJob?.cancel()
        _uiState.update {
            it.copy(
                isScanning = true,
                isLoading = false,
                scannedIsbn = null,
                scannedBook = null,
                isInLibrary = false,
                addedMessage = null,
                error = null,
                scanMode = mode,
                detectedText = "",
                isSearchingByText = false,
            )
        }
    }

    fun onBarcodeScanned(isbn: String) {
        val state = _uiState.value
        val shouldIgnore =
            state.scanMode != ScanMode.BARCODE ||
                state.isLoading ||
                state.scannedIsbn == isbn
        if (shouldIgnore) {
            return
        }
        textSearchJob?.cancel()
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
                detectedText = "",
                isSearchingByText = false,
            )
        }

        viewModelScope.launch {
            val book = bookRepository.getBookByIsbn(isbn)
            if (book != null) {
                _uiState.update { it.copy(isLoading = false, scannedBook = book) }
                observeLibraryStatus(book.id)
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

    fun onTextDetected(text: String) {
        val normalizedText = text.replace("\n", " ").trim()
        val state = _uiState.value
        val isDuplicateSearch =
            state.detectedText == normalizedText &&
                (state.isSearchingByText || textSearchJob?.isActive == true)
        val shouldIgnore =
            state.scanMode != ScanMode.COVER ||
                state.scannedBook != null ||
                state.isLoading ||
                normalizedText.length < MIN_TEXT_LENGTH ||
                isDuplicateSearch
        if (shouldIgnore) {
            return
        }

        _uiState.update {
            it.copy(
                detectedText = normalizedText,
                error = null,
                addedMessage = null,
                scannedIsbn = null,
            )
        }
        searchByText(normalizedText)
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
        textSearchJob?.cancel()
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
                detectedText = "",
                isSearchingByText = false,
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
                isSearchingByText = false,
            )
        }
    }

    private fun searchByText(text: String) {
        textSearchJob?.cancel()
        textSearchJob = viewModelScope.launch {
            delay(TEXT_SEARCH_DEBOUNCE_MS)
            _uiState.update {
                it.copy(
                    isSearchingByText = true,
                    error = null,
                )
            }

            runCatching {
                bookRepository.searchBooks(query = text, maxResults = 1).firstOrNull()
            }.onSuccess { book ->
                if (book != null) {
                    val inLibrary = libraryRepository.isBookInLibrary(book.id).first()
                    _uiState.update {
                        it.copy(
                            scannedBook = book,
                            isInLibrary = inLibrary,
                            isSearchingByText = false,
                            detectedText = "",
                            isScanning = false,
                        )
                    }
                    observeLibraryStatus(book.id)
                } else {
                    _uiState.update { it.copy(isSearchingByText = false) }
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSearchingByText = false,
                        error = throwable.message ?: SEARCH_FAILED_MESSAGE,
                    )
                }
            }
        }
    }

    private fun observeLibraryStatus(bookId: String) {
        libraryObserverJob?.cancel()
        libraryObserverJob = viewModelScope.launch {
            libraryRepository.isBookInLibrary(bookId).collect { inLibrary ->
                _uiState.update { currentState ->
                    currentState.copy(isInLibrary = inLibrary)
                }
            }
        }
    }

    private fun buildAddedMessage(status: ReadingStatus): String =
        if (status == ReadingStatus.READ) {
            READS_MESSAGE
        } else {
            WISHLIST_MESSAGE
        }
}
