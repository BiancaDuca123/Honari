package com.honari.app.domain.model

data class Book(
    val id: String = "",
    val title: String = "",
    val authors: List<String> = emptyList(),
    val description: String = "",
    val imageUrl: String = "",
    val isbn: String = "",
    val categories: List<String> = emptyList(),
    val pageCount: Int = 0,
    val publishedDate: String = "",
    val averageRating: Float = 0f,
    val ratingsCount: Int = 0,
    val publisher: String = "",
    val language: String = "",
    // Library fields — null when the book is not in the user's library
    val libraryStatus: ReadingStatus? = null,
    val addedAt: Long? = null,
    val userRating: Float = 0f,
)
