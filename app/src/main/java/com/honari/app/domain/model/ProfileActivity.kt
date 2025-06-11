package com.honari.app.domain.model

/**
 * Profile activity.
 */
data class ProfileActivity(
    val type: String,
    val description: String,
    val date: String,
    val rating: Float?
)