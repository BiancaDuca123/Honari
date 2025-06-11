package com.honari.app.presentation.screens.reading

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Timer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.CurrentBook
import com.honari.app.domain.model.ReadingGoal
import com.honari.app.domain.model.ReadingUiState
import com.honari.app.domain.model.RecentQuote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Reading screen.
 */
@HiltViewModel
class ReadingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingUiState())
    val uiState: StateFlow<ReadingUiState> = _uiState.asStateFlow()

    init {
        loadReadingData()
    }

    private fun loadReadingData() {
        viewModelScope.launch {
            _uiState.value = ReadingUiState(
                currentBooks = listOf(
                    CurrentBook(
                        id = "1",
                        title = "Norwegian Wood",
                        author = "Haruki Murakami",
                        progress = 68,
                        currentPage = 245,
                        totalPages = 360,
                        imageUrl = "https://images.pexels.com/photos/1130980/pexels-photo-1130980.jpeg?auto=compress&cs=tinysrgb&w=300&h=400",
                        readingTime = 25,
                        lastSession = "2 hours ago",
                        pagesLeft = 115,
                        estimatedTime = "4h 35m"
                    ),
                    CurrentBook(
                        id = "2",
                        title = "Klara and the Sun",
                        author = "Kazuo Ishiguro",
                        progress = 34,
                        currentPage = 102,
                        totalPages = 300,
                        imageUrl = "https://images.pexels.com/photos/2067569/pexels-photo-2067569.jpeg?auto=compress&cs=tinysrgb&w=300&h=400",
                        readingTime = 15,
                        lastSession = "1 day ago",
                        pagesLeft = 198,
                        estimatedTime = "7h 20m"
                    )
                ),
                todayGoals = listOf(
                    ReadingGoal("daily", 30, 18, "pages", Icons.Default.MenuBook),
                    ReadingGoal("weekly", 5, 3, "hours", Icons.Default.Timer),
                    ReadingGoal("monthly", 3, 1, "books", Icons.Default.Flag)
                ),
                recentQuotes = listOf(
                    RecentQuote(
                        id = "1",
                        text = "Memory is a funny thing. When I try to remember things from the past, I often can't remember what happened when.",
                        bookTitle = "Norwegian Wood",
                        author = "Haruki Murakami",
                        page = 178,
                        dateAdded = "Today"
                    ),
                    RecentQuote(
                        id = "2",
                        text = "The heart's memory eliminates the bad and magnifies the good.",
                        bookTitle = "The Seven Husbands of Evelyn Hugo",
                        author = "Taylor Jenkins Reid",
                        page = 45,
                        dateAdded = "Yesterday"
                    )
                )
            )
        }
    }

    fun toggleSession() {
        _uiState.value = _uiState.value.copy(
            isSessionActive = !_uiState.value.isSessionActive
        )
    }
}
