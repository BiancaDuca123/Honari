package com.honari.app.domain.model

/**
 * Current book model.
 */
data class CurrentBook(
    val id: String,
    val title: String,
    val author: String,
    val progress: Int,
    val currentPage: Int,
    val totalPages: Int,
    val imageUrl: String,
    val readingTime: Int,
    val lastSession: String,
    val pagesLeft: Int,
    val estimatedTime: String
)