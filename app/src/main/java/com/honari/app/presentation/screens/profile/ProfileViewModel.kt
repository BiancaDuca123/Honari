package com.honari.app.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.repository.AuthRepository
import com.honari.app.domain.repository.LibraryRepository
import com.honari.app.presentation.theme.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val libraryRepository: LibraryRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                authRepository.getCurrentUser()
                    .flatMapLatest { user ->
                        if (user == null) {
                            flowOf(null to emptyList())
                        } else {
                            libraryRepository.getUserLibrary(user.id).map { books -> user to books }
                        }
                    },
                userPreferences.readingGoalFlow
            ) { (user, books), goal ->
                Triple(user, books, goal)
            }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { (user, books, goal) ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            totalBooks = books.size,
                            currentlyReading = books.count { b ->
                                b.status ==
                                    ReadingStatus.READING.name
                            },
                            booksRead = books.count { b ->
                                b.status == ReadingStatus.FINISHED.name
                            },
                            wantToRead = books.count { b ->
                                b.status ==
                                    ReadingStatus.WANT_TO_READ.name
                            },
                            readingGoal = goal
                        )
                    }
                }
        }
    }

    fun updateReadingGoal(goal: Int) {
        viewModelScope.launch { userPreferences.setReadingGoal(goal) }
    }

    fun signOut(onSignedOut: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onSignedOut()
        }
    }
}
