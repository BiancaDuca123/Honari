package com.honari.app.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Yearly stat item.
 */
data class YearlyStat(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)