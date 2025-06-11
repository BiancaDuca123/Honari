package com.honari.app.domain.model

/**
 * Data class representing a user's circle.
 */
data class MyCircle(
    val id: String,
    val name: String,
    val description: String,
    val memberCount: Int,
    val bookCount: Int,
    val imageUrl: String,
    val isActive: Boolean,
    val lastActivity: String,
    val currentBook: String
)
