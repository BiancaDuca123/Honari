package com.honari.app.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.User
import com.honari.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null,
    val successMessage: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _uiState.update { it.copy(isAuthenticated = user != null, currentUser = user) }
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.signInWithGoogle(idToken)
            _uiState.update { state ->
                if (result.isSuccess) {
                    state.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = result.getOrNull(),
                    )
                } else {
                    state.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Sign-in failed",
                    )
                }
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.signInWithEmail(email, password)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(isLoading = false, isAuthenticated = true, currentUser = user)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Sign-in failed")
                    }
                }
        }
    }

    fun registerWithEmail(email: String, displayName: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.registerWithEmail(email, displayName, password)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(isLoading = false, isAuthenticated = true, currentUser = user)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Registration failed")
                    }
                }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.sendPasswordReset(email)
                .onSuccess {
                    _uiState.update {
                        it.copy(isLoading = false, successMessage = "Reset link sent! Check your inbox.")
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Couldn't send reset email")
                    }
                }
        }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(isAuthenticated = false, currentUser = null) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
