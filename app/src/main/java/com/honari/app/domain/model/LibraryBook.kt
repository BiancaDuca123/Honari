package com.honari.app.domain.model

/**
 * Book model for library.
 */
data class LibraryBook(
    val id: String,
    val title: String,
    val author: String,
    val imageUrl: String,
    val status: ReadingStatus,
    val progress: Int? = null,
    val rating: Float? = null,
    val tags: List<String> = emptyList(),
    val dateAdded: String
)
