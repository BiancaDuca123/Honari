package com.honari.app.domain.model

/**
 * Domain model representing a book entity.
 * Clean architecture - no framework dependencies.
 */
data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val rating: Float = 0f,
    val imageUrl: String = "",
    val mood: String = "",
    val readers: Int = 0,
    val description: String = "",
    val category: String? = null,
    val trend: String? = null,
    val featured: Boolean = false
)