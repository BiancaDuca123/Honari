package com.honari.app.domain.model

/**
 * UI state for the Circles screen.
 */
data class CirclesUiState(
    val isLoading: Boolean = false,
    val myCircles: List<MyCircle> = emptyList(),
    val suggestedCircles: List<SuggestedCircle> = emptyList(),
    val recentActivities: List<CircleActivity> = emptyList(),
    val error: String? = null
)