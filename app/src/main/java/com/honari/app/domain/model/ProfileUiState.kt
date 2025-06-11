package com.honari.app.domain.model

/**
 * Profile UI state.
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val stats: ProfileStats = ProfileStats(),
    val yearlyStats: List<YearlyStat> = emptyList(),
    val milestones: List<Milestone> = emptyList(),
    val favoriteGenres: List<GenrePreference> = emptyList(),
    val recentActivities: List<ProfileActivity> = emptyList(),
    val error: String? = null
)