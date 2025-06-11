package com.honari.app.presentation.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.repository.AuthRepository
import com.honari.app.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    private var deletionJob: Job? = null

    init {
        loadLibrary()
    }

    private fun loadLibrary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepository.getCurrentUser()
                .flatMapLatest { user ->
                    if (user == null) {
                        flowOf(emptyList())
                    } else {
                        libraryRepository.getUserLibrary(user.id)
                    }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error =
                            e.message ?: "Failed to load library"
                        )
                    }
                }
                .collect { books ->
                    _uiState.update { it.copy(isLoading = false, books = books, error = null) }
                }
        }
    }

    fun setFilter(status: ReadingStatus?) {
        _uiState.update { it.copy(selectedFilter = status) }
    }

    fun updateBookStatus(bookId: String, status: ReadingStatus) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser().first() ?: return@launch
            libraryRepository.updateStatus(user.id, bookId, status)
        }
    }

    /** Update reading progress (0–100 %). */
    fun updateProgress(bookId: String, progress: Int) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser().first() ?: return@launch
            libraryRepository.updateProgress(user.id, bookId, progress)
        }
    }

    // ── Swipe-to-delete with Undo ────────────────────────────────────────────

    /**
     * Hides [bookId] from the list immediately and schedules a permanent Firestore
     * deletion after [undoWindowMs] ms.  Call [undoDeletion] to cancel.
     */
    fun markForDeletion(bookId: String, undoWindowMs: Long = 4_000L) {
        deletionJob?.cancel()
        _uiState.update { it.copy(pendingDeleteId = bookId) }
        deletionJob = viewModelScope.launch {
            delay(undoWindowMs)
            commitDeletion()
        }
    }

    /** Cancels the scheduled deletion and restores the book in the list. */
    fun undoDeletion() {
        deletionJob?.cancel()
        deletionJob = null
        _uiState.update { it.copy(pendingDeleteId = null) }
    }

    private fun commitDeletion() {
        val bookId = _uiState.value.pendingDeleteId ?: return
        viewModelScope.launch {
            val user = authRepository.getCurrentUser().first() ?: return@launch
            libraryRepository.removeBook(user.id, bookId)
            _uiState.update { it.copy(pendingDeleteId = null) }
        }
    }
}
