package com.honari.app.presentation.screens.scan

import com.honari.app.domain.model.Book

/**
 * Represents the current state of the book scanner screen.
 */
data class ScanUiState(
    /** Camera is active and scanning for a barcode. */
    val isScanning: Boolean = true,
    /** A network request is in-flight after a barcode was detected. */
    val isLoading: Boolean = false,
    /** Successfully identified book; shown in the result sheet. */
    val scannedBook: Book? = null,
    /** Human-readable error message, null when there is no error. */
    val error: String? = null,
    /** Whether the manual-search panel is visible. */
    val showManualSearch: Boolean = false,
    /** Current value of the manual-search text field. */
    val manualQuery: String = "",
    /** Results returned by a manual-search query. */
    val searchResults: List<Book> = emptyList(),
    /** Whether a book was successfully added to the library (triggers a Snackbar). */
    val bookAddedToLibrary: Boolean = false
)
