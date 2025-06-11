package com.honari.app.domain.model

/**
 * Data class representing a suggested circle.
 */
data class SuggestedCircle(
    val id: String,
    val name: String,
    val description: String,
    val memberCount: Int,
    val bookCount: Int,
    val imageUrl: String,
    val commonBooks: Int,
    val matchPercent: Int
)
