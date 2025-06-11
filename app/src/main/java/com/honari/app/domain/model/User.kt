package com.honari.app.domain.model

/**
 * Domain model representing a user.
 * All parameters have default values for Firebase deserialization.
 */
data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val profileImageUrl: String? = null,
    val favoriteGenres: List<String> = emptyList(),
    val readingGoal: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
