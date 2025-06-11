package com.honari.app.presentation.screens.discover

import DiscoverUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.Mood
import com.honari.app.domain.repository.BookRepository
import com.honari.app.presentation.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Discover screen.
 * Manages UI state and business logic following MVVM pattern.
 */
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    /**
     * Loads all data needed for the Discover screen.
     * Uses coroutines for concurrent data fetching.
     */
    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load featured books
            launch {
                bookRepository.getFeaturedBooks().collect { books ->
                    _uiState.value = _uiState.value.copy(featuredBooks = books)
                }
            }

            // Load trending books
            launch {
                bookRepository.getTrendingBooks().collect { books ->
                    _uiState.value = _uiState.value.copy(trendingBooks = books)
                }
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    /**
     * Returns predefined moods.
     * In a real app, this might come from a repository.
     */
    fun getMoods(): List<Mood> = listOf(
        Mood(
            id = 1,
            name = "Melancholic",
            iconName = "moon",
            color = MoodMelancholic,
            description = "Introspective & thoughtful",
            gradientColors = listOf(MoodMelancholic, MoodMelancholiLight)
        ),
        Mood(
            id = 2,
            name = "Dreamy",
            iconName = "cloud",
            color = MoodDreamy,
            description = "Ethereal & imaginative",
            gradientColors = listOf(MoodDreamy, MoodDreamyLight)
        ),
        Mood(
            id = 3,
            name = "Nostalgic",
            iconName = "sunset",
            color = MoodNostalgic,
            description = "Wistful & reminiscent",
            gradientColors = listOf(MoodNostalgic, MoodNostalgicLight)
        ),
        Mood(
            id = 4,
            name = "Contemplative",
            iconName = "coffee",
            color = MoodContemplative,
            description = "Deep & philosophical",
            gradientColors = listOf(MoodContemplative, MoodContemplativeLight)
        ),
        Mood(
            id = 5,
            name = "Romantic",
            iconName = "heart",
            color = MoodRomantic,
            description = "Passionate & tender",
            gradientColors = listOf(MoodRomantic, MoodRomanticLight)
        )
    )

    /**
     * Handles mood selection.
     */
    fun onMoodSelected(mood: Mood) {
        viewModelScope.launch {
            bookRepository.getBooksByMood(mood.name).collect { books ->
                // Handle mood-based book filtering
            }
        }
    }

    /**
     * Gets the current greeting based on time of day.
     */
    fun getGreeting(): String {
        val hour = java.time.LocalTime.now().hour
        return when (hour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }
}