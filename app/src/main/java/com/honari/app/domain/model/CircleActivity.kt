package com.honari.app.domain.model

/**
 * Data class representing a circle activity.
 */
data class CircleActivity(
    val id: String,
    val type: ActivityType,
    val userName: String,
    val userAvatar: String,
    val action: String,
    val bookTitle: String,
    val circleName: String,
    val timestamp: String
)