package com.honari.app.presentation.screens.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private var libraryObserverJob: Job? = null

    fun onBarcodeScanned(isbn: String) {
        if (_uiState.value.isLoading || _uiState.value.scannedIsbn == isbn) return
        _uiState.update { it.copy(isLoading = true, scannedIsbn = isbn, isScanning = false, error = null) }

        viewModelScope.launch {
            val book = bookRepository.getBookByIsbn(isbn)
            if (book != null) {
                _uiState.update { it.copy(isLoading = false, scannedBook = book) }
                libraryObserverJob?.cancel()
                libraryObserverJob = viewModelScope.launch {
                    libraryRepository.isBookInLibrary(book.id).collect { inLibrary ->
                        _uiState.update { it.copy(isInLibrary = inLibrary) }
                    }
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Book not found for this barcode") }
            }
        }
    }

    fun addToLibrary(status: ReadingStatus) {
        val book = _uiState.value.scannedBook ?: return
        viewModelScope.launch {
            libraryRepository.addBook(book.copy(libraryStatus = status, addedAt = System.currentTimeMillis()))
            _uiState.update { it.copy(isInLibrary = true, addedStatus = status) }
        }
    }

    fun dismissSheet() {
        libraryObserverJob?.cancel()
        _uiState.update { ScannerUiState(isScanning = true) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null, isScanning = true) }
    }
}
