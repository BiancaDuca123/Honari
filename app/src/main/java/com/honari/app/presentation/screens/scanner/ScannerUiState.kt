package com.honari.app.presentation.screens.scanner

import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus

data class ScannerUiState(
    val isScanning: Boolean = true,
    val isLoading: Boolean = false,
    val scannedIsbn: String? = null,
    val scannedBook: Book? = null,
    val isInLibrary: Boolean = false,
    val error: String? = null,
    val addedStatus: ReadingStatus? = null,
)
