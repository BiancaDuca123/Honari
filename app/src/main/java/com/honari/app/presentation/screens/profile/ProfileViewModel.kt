package com.honari.app.presentation.screens.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.GenrePreference
import com.honari.app.domain.model.Milestone
import com.honari.app.domain.model.ProfileActivity
import com.honari.app.domain.model.ProfileStats
import com.honari.app.domain.model.ProfileUiState
import com.honari.app.domain.model.YearlyStat
import com.honari.app.presentation.theme.MoodDreamy
import com.honari.app.presentation.theme.MoodNostalgic
import com.honari.app.presentation.theme.MoodRomantic
import com.honari.app.presentation.theme.PrimaryColor
import com.honari.app.presentation.theme.RatingStarColor
import com.honari.app.presentation.theme.TrendingColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Profile screen.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState(
                stats = ProfileStats(
                    totalBooksRead = 42,
                    booksThisYear = 15,
                    reviewsWritten = 23,
                    memberSince = "January 2024"
                ),
                yearlyStats = listOf(
                    YearlyStat("Books Read", "28", Icons.Default.MenuBook, PrimaryColor),
                    YearlyStat("Reading Hours", "156", Icons.Default.Timer, MoodDreamy),
                    YearlyStat("Avg Rating", "4.3", Icons.Default.Star, RatingStarColor),
                    YearlyStat("Streak Days", "47", Icons.Default.TrendingUp, TrendingColor)
                ),
                milestones = listOf(
                    Milestone("1", "Night Reader", "100 hours of evening reading", true, "🌙"),
                    Milestone("2", "Quote Collector", "50 quotes captured", true, "💫"),
                    Milestone("3", "Circle Leader", "Active in 3+ circles", false, "👥"),
                    Milestone("4", "Literary Explorer", "10 different genres", true, "🗺️")
                ),
                favoriteGenres = listOf(
                    GenrePreference("Literary Fiction", 35, PrimaryColor),
                    GenrePreference("Philosophy", 25, MoodDreamy),
                    GenrePreference("Contemporary", 20, TrendingColor),
                    GenrePreference("Sci-Fi", 12, MoodNostalgic),
                    GenrePreference("Mystery", 8, MoodRomantic)
                ),
                recentActivities = listOf(
                    ProfileActivity(
                        "finished",
                        "Finished reading \"The Midnight Library\"",
                        "2 days ago",
                        4.8f
                    ),
                    ProfileActivity(
                        "joined",
                        "Joined circle \"Tokyo Night Readers\"",
                        "1 week ago",
                        null
                    ),
                    ProfileActivity(
                        "quote",
                        "Captured a quote from \"Norwegian Wood\"",
                        "3 days ago",
                        null
                    ),
                    ProfileActivity(
                        "started",
                        "Started reading \"Klara and the Sun\"",
                        "5 days ago",
                        null
                    )
                )
            )
        }
    }
}
