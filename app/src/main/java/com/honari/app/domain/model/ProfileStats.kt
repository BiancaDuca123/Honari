package com.honari.app.domain.model

/**
 * User profile statistics.
 */
data class ProfileStats(
    val totalBooksRead: Int = 0,
    val booksThisYear: Int = 0,
    val reviewsWritten: Int = 0,
    val memberSince: String = ""
)