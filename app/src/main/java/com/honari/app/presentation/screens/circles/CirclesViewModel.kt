package com.honari.app.presentation.screens.circles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.ActivityType
import com.honari.app.domain.model.CircleActivity
import com.honari.app.domain.model.CirclesUiState
import com.honari.app.domain.model.MyCircle
import com.honari.app.domain.model.SuggestedCircle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Circles screen.
 * Manages reading circles and social features.
 */
@HiltViewModel
class CirclesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CirclesUiState())
    val uiState: StateFlow<CirclesUiState> = _uiState.asStateFlow()

    init {
        loadCirclesData()
    }

    private fun loadCirclesData() {
        viewModelScope.launch {
            // TODO: Load actual data from repository
            _uiState.value = _uiState.value.copy(
                myCircles = getMockMyCircles(),
                suggestedCircles = getMockSuggestedCircles(),
                recentActivities = getMockActivities()
            )
        }
    }

    fun joinCircle(circleId: String) {
        // TODO: Implement join circle functionality
    }

    private fun getMockMyCircles(): List<MyCircle> {
        return listOf(
            MyCircle(
                id = "1",
                name = "Quiet Reads",
                description = "For books that whisper to the soul",
                memberCount = 127,
                bookCount = 23,
                imageUrl = "https://images.pexels.com/photos/1130980/pexels-photo-1130980.jpeg?auto=compress&cs=tinysrgb&w=100&h=100",
                isActive = true,
                lastActivity = "2h ago",
                currentBook = "Norwegian Wood"
            ),
            MyCircle(
                id = "2",
                name = "Murakami Lovers",
                description = "Exploring surreal worlds together",
                memberCount = 89,
                bookCount = 15,
                imageUrl = "https://images.pexels.com/photos/1926988/pexels-photo-1926988.jpeg?auto=compress&cs=tinysrgb&w=100&h=100",
                isActive = true,
                lastActivity = "5h ago",
                currentBook = "Kafka on the Shore"
            )
        )
    }

    private fun getMockSuggestedCircles(): List<SuggestedCircle> {
        return listOf(
            SuggestedCircle(
                id = "3",
                name = "Tokyo Night Readers",
                description = "Late-night literary discussions",
                memberCount = 245,
                bookCount = 34,
                imageUrl = "https://images.pexels.com/photos/1370298/pexels-photo-1370298.jpeg?auto=compress&cs=tinysrgb&w=100&h=100",
                commonBooks = 8,
                matchPercent = 87
            ),
            SuggestedCircle(
                id = "4",
                name = "Philosophical Souls",
                description = "Books that make us think deeply",
                memberCount = 156,
                bookCount = 28,
                imageUrl = "https://images.pexels.com/photos/2067569/pexels-photo-2067569.jpeg?auto=compress&cs=tinysrgb&w=100&h=100",
                commonBooks = 12,
                matchPercent = 92
            )
        )
    }

    private fun getMockActivities(): List<CircleActivity> {
        return listOf(
            CircleActivity(
                id = "1",
                type = ActivityType.DISCUSSION,
                userName = "Elena",
                userAvatar = "https://images.pexels.com/photos/1130626/pexels-photo-1130626.jpeg?auto=compress&cs=tinysrgb&w=50&h=50",
                action = "started a discussion about",
                bookTitle = "The Wind-Up Bird Chronicle",
                circleName = "Murakami Lovers",
                timestamp = "15 min ago"
            ),
            CircleActivity(
                id = "2",
                type = ActivityType.RECOMMENDATION,
                userName = "Marcus",
                userAvatar = "https://images.pexels.com/photos/1222271/pexels-photo-1222271.jpeg?auto=compress&cs=tinysrgb&w=50&h=50",
                action = "recommended",
                bookTitle = "Breasts and Eggs",
                circleName = "Quiet Reads",
                timestamp = "1h ago"
            ),
            CircleActivity(
                id = "3",
                type = ActivityType.QUOTE,
                userName = "Yuki",
                userAvatar = "https://images.pexels.com/photos/1239291/pexels-photo-1239291.jpeg?auto=compress&cs=tinysrgb&w=50&h=50",
                action = "shared a quote from",
                bookTitle = "Norwegian Wood",
                circleName = "Quiet Reads",
                timestamp = "2h ago"
            )
        )
    }
}