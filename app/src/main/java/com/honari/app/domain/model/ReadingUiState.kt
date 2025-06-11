package com.honari.app.domain.model

/**
 * Reading UI state.
 */
data class ReadingUiState(
    val isLoading: Boolean = false,
    val currentBooks: List<CurrentBook> = emptyList(),
    val todayGoals: List<ReadingGoal> = emptyList(),
    val recentQuotes: List<RecentQuote> = emptyList(),
    val isSessionActive: Boolean = false,
    val error: String? = null
)
