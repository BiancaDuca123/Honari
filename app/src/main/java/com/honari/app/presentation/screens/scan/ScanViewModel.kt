package com.honari.app.presentation.screens.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.repository.AuthRepository
import com.honari.app.domain.repository.BookLookupRepository
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
 * ViewModel for the Scan screen.
 *
 * Responsibilities:
 * - Receive ISBN barcodes from [BarcodeAnalyzer] and look up the book.
 * - Handle manual title/author search as a fallback.
 * - Add a recognized book to the user's library.
 */
@HiltViewModel
class ScanViewModel @Inject constructor(
    private val bookLookupRepository: BookLookupRepository,
    private val addBookToLibraryUseCase: AddBookToLibraryUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    // -------------------------------------------------------------------------
    // Barcode / ISBN scanner
    // -------------------------------------------------------------------------

    /** Called by [BarcodeAnalyzer] when an ISBN barcode is detected. */
    fun onBarcodeDetected(isbn: String) {
        if (_uiState.value.isLoading || _uiState.value.scannedBook != null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = false, isLoading = true, error = null) }

            val result = bookLookupRepository.searchByIsbn(isbn)

            _uiState.update {
                if (result.isSuccess) {
                    it.copy(isLoading = false, scannedBook = result.getOrNull())
                } else {
                    it.copy(
                        isLoading = false,
                        isScanning = true,
                        error = "Book not found. Try searching manually."
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Manual search
    // -------------------------------------------------------------------------

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(manualQuery = query) }
    }

    fun onSearch() {
        val query = _uiState.value.manualQuery.trim()
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, searchResults = emptyList()) }

            val result = bookLookupRepository.searchByQuery(query)

            _uiState.update {
                if (result.isSuccess) {
                    it.copy(isLoading = false, searchResults = result.getOrDefault(emptyList()))
                } else {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Search failed."
                    )
                }
            }
        }
    }

    fun onSearchResultSelected(book: Book) {
        _uiState.update {
            it.copy(
                scannedBook = book,
                showManualSearch = false,
                searchResults = emptyList()
            )
        }
    }

    fun toggleManualSearch() {
        _uiState.update { it.copy(showManualSearch = !it.showManualSearch, error = null) }
    }

    // -------------------------------------------------------------------------
    // Library actions
    // -------------------------------------------------------------------------

    fun addToLibrary(book: Book, status: ReadingStatus = ReadingStatus.WANT_TO_READ) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser().first() ?: return@launch

            val result = addBookToLibraryUseCase(
                userId = user.id,
                book = book,
                status = status
            )

            _uiState.update {
                it.copy(bookAddedToLibrary = result.isSuccess)
            }
        }
    }

    // -------------------------------------------------------------------------
    // Reset / navigation
    // -------------------------------------------------------------------------

    fun resetScan() {
        _uiState.update {
            ScanUiState()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun acknowledgeLibraryAdd() {
        _uiState.update { it.copy(bookAddedToLibrary = false) }
    }
}
