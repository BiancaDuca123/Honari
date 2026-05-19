package com.honari.app.presentation.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibraryUiState(
    val isLoading: Boolean = false,
    val allBooks: List<Book> = emptyList(),
    val selectedTab: ReadingStatus = ReadingStatus.READ,
    val error: String? = null,
) {
    val displayedBooks: List<Book>
        get() = allBooks.filter { it.libraryStatus == selectedTab }
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            libraryRepository.getAllBooks().collect { books ->
                _uiState.update { it.copy(isLoading = false, allBooks = books) }
            }
        }
    }

    fun selectTab(status: ReadingStatus) {
        _uiState.update { it.copy(selectedTab = status) }
    }

    fun removeBook(bookId: String) {
        viewModelScope.launch { libraryRepository.removeBook(bookId) }
    }

    fun moveBook(bookId: String, status: ReadingStatus) {
        viewModelScope.launch { libraryRepository.updateStatus(bookId, status) }
    }
}
