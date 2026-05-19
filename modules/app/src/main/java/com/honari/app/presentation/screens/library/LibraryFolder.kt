package com.honari.app.presentation.screens.library

import com.honari.app.domain.model.Book

data class LibraryFolder(
    val name: String,
    val filter: LibraryFilter,
    val books: List<Book>,
)
