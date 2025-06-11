package com.honari.app.presentation.screens.library

import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus

data class LibraryUiState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val selectedFilter: ReadingStatus? = null,
    val error: String? = null,
    val pendingDeleteId: String? = null
) {
    val filtered: List<Book>
        get() {
            val visible = books.filter { it.id != pendingDeleteId }
            return if (selectedFilter == null) {
                visible
            } else {
                visible.filter { it.status == selectedFilter.name }
            }
        }
}
