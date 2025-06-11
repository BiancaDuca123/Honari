package com.honari.app.domain.model

/**
 * Data class representing a reading session.
 */
data class ReadingSession(
    val bookTitle: String,
    val date: String,
    val minutes: Int,
    val pagesRead: Int
)