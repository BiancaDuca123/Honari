package com.honari.app.presentation.screens.library

import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus

data class LibraryUiState(
    val isLoading: Boolean = false,
    val allBooks: List<Book> = emptyList(),
    val selectedTab: ReadingStatus = ReadingStatus.READ,
    val error: String? = null,
) {
    val displayedBooks: List<Book>
        get() = allBooks.filter { it.libraryStatus == selectedTab }
}
