package com.honari.app.presentation.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.LibraryBook
import com.honari.app.domain.model.LibraryStats
import com.honari.app.domain.model.LibraryTab
import com.honari.app.domain.model.LibraryUiState
import com.honari.app.domain.model.ReadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Library screen.
 */
@HiltViewModel
class LibraryViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadLibraryData()
    }

    private fun loadLibraryData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Mock data
            val books = getMockBooks()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                allBooks = books,
                currentlyReading = books.filter { it.status == ReadingStatus.READING },
                finishedBooks = books.filter { it.status == ReadingStatus.FINISHED },
                wantToRead = books.filter { it.status == ReadingStatus.WANT_TO_READ },
                stats = LibraryStats(
                    totalBooks = 47,
                    booksThisYear = 12,
                    avgRating = 4.3f,
                    pagesRead = 847
                ),
                tabCounts = mapOf(
                    LibraryTab.ALL to 47,
                    LibraryTab.READING to 3,
                    LibraryTab.READ to 28,
                    LibraryTab.TO_READ to 16
                )
            )
        }
    }

    private fun getMockBooks(): List<LibraryBook> {
        return listOf(
            LibraryBook(
                id = "1",
                title = "Norwegian Wood",
                author = "Haruki Murakami",
                status = ReadingStatus.READING,
                progress = 68,
                imageUrl = "https://images.pexels.com/photos/1130980/pexels-photo-1130980.jpeg?auto=compress&cs=tinysrgb&w=200&h=280",
                tags = listOf("melancholic", "coming-of-age"),
                dateAdded = "Jan 15, 2024"
            ),
            LibraryBook(
                id = "2",
                title = "The Midnight Library",
                author = "Matt Haig",
                status = ReadingStatus.FINISHED,
                rating = 4.8f,
                imageUrl = "https://images.pexels.com/photos/1926988/pexels-photo-1926988.jpeg?auto=compress&cs=tinysrgb&w=200&h=280",
                tags = listOf("philosophical", "uplifting"),
                dateAdded = "Dec 8, 2023"
            ),
            LibraryBook(
                id = "3",
                title = "Circe",
                author = "Madeline Miller",
                status = ReadingStatus.WANT_TO_READ,
                imageUrl = "https://images.pexels.com/photos/1370298/pexels-photo-1370298.jpeg?auto=compress&cs=tinysrgb&w=200&h=280",
                tags = listOf("mythology", "fantasy"),
                dateAdded = "Jan 20, 2024"
            ),
            LibraryBook(
                id = "4",
                title = "Klara and the Sun",
                author = "Kazuo Ishiguro",
                status = ReadingStatus.READING,
                progress = 34,
                imageUrl = "https://images.pexels.com/photos/2067569/pexels-photo-2067569.jpeg?auto=compress&cs=tinysrgb&w=200&h=280",
                tags = listOf("sci-fi", "emotional"),
                dateAdded = "Jan 10, 2024"
            )
        )
    }
}