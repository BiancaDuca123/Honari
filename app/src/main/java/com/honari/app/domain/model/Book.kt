package com.honari.app.domain.model

/**
 * Domain model representing a book entity.
 * Fields are `var` so Firestore can deserialize via bean reflection.
 */
data class Book(
    var id: String = "",
    var title: String = "",
    var author: String = "",
    var rating: Float = 0f,
    var imageUrl: String = "",
    var readers: Int = 0,
    var description: String = "",
    var category: String? = null,
    var isbn: String = "",
    var pageCount: Int = 0,
    var publisher: String = "",
    var publishedDate: String = "",
    // User-specific library fields
    var status: String = "",
    var progress: Int = 0,
    var userRating: Float = 0f,
    var dateAdded: String = ""
)
