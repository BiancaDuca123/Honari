package com.honari.app.domain.model

/**
 * Milestone item.
 */
data class Milestone(
    val id: String,
    val title: String,
    val description: String,
    val achieved: Boolean,
    val icon: String
)
