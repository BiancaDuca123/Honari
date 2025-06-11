package com.honari.app.domain.model

/**
 * Library statistics.
 */
data class LibraryStats(
    val totalBooks: Int = 0,
    val booksThisYear: Int = 0,
    val avgRating: Float = 0f,
    val pagesRead: Int = 0
)