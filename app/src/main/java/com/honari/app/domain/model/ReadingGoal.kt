package com.honari.app.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Reading goal model.
 */
data class ReadingGoal(
    val type: String,
    val target: Int,
    val current: Int,
    val unit: String,
    val icon: ImageVector
)