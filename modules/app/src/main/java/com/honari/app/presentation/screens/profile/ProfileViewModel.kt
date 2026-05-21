package com.honari.app.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.data.local.preferences.PreferencesDataSource
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.model.User
import com.honari.app.domain.repository.AuthRepository
import com.honari.app.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val totalRead: Int = 0,
    val wantToRead: Int = 0,
    val allBooks: List<Book> = emptyList(),
    val isDarkMode: Boolean = false,
    val showSettings: Boolean = false,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val libraryRepository: LibraryRepository,
    private val preferences: PreferencesDataSource,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isDarkMode = preferences.isDarkMode))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeUser()
        observeBooks()
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }

    fun setDarkMode(enabled: Boolean) {
        preferences.isDarkMode = enabled
        _uiState.update { it.copy(isDarkMode = enabled) }
    }

    fun toggleSettings() {
        _uiState.update { it.copy(showSettings = !it.showSettings) }
    }

    private fun observeUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
    }

    private fun observeBooks() {
        viewModelScope.launch {
            libraryRepository.getAllBooks().collect { books ->
                val totalRead = books.count { book ->
                    book.libraryStatus == ReadingStatus.READ
                }
                val wantToRead = books.count { book ->
                    book.libraryStatus == ReadingStatus.WANT_TO_READ
                }
                _uiState.update {
                    it.copy(
                        totalRead = totalRead,
                        wantToRead = wantToRead,
                        allBooks = books,
                    )
                }
            }
        }
    }
}
