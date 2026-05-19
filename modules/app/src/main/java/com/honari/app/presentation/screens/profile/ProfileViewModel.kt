package com.honari.app.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.model.User
import com.honari.app.domain.repository.AuthRepository
import com.honari.app.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val totalRead: Int = 0,
    val wantToRead: Int = 0,
    val isDarkMode: Boolean = false,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                authRepository.getCurrentUser(),
                libraryRepository.getAllBooks(),
            ) { user, books ->
                ProfileUiState(
                    user = user,
                    totalRead = books.count { it.libraryStatus == ReadingStatus.READ },
                    wantToRead = books.count { it.libraryStatus == ReadingStatus.WANT_TO_READ },
                    isDarkMode = _uiState.value.isDarkMode,
                )
            }.collect { state -> _uiState.update { state } }
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }
}
