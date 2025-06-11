package com.honari.app.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Domain model representing a reading mood.
 */
data class Mood(
    val id: Int,
    val name: String,
    val iconName: String,
    val color: Color,
    val description: String,
    val gradientColors: List<Color>
)
