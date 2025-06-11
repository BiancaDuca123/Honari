package com.honari.app.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Genre preference.
 */
data class GenrePreference(
    val name: String,
    val percentage: Int,
    val color: Color
)