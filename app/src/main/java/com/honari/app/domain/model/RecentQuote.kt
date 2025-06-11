package com.honari.app.domain.model

/**
 * Recent quote model.
 */
data class RecentQuote(
    val id: String,
    val text: String,
    val bookTitle: String,
    val author: String,
    val page: Int,
    val dateAdded: String
)