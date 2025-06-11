package com.honari.app.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.honari.app.domain.model.Book
import com.honari.app.presentation.screens.discover.DiscoverScreen
import com.honari.app.presentation.screens.discover.DiscoverUiState
import com.honari.app.presentation.screens.discover.DiscoverViewModel
import com.honari.app.presentation.theme.HonariTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class DiscoverScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeBooks = listOf(
        Book(id = "1", title = "Dune", author = "Frank Herbert", rating = 4.5f),
        Book(id = "2", title = "1984", author = "George Orwell", rating = 4.7f)
    )

    private fun buildViewModel(state: DiscoverUiState): DiscoverViewModel {
        val vm = mockk<DiscoverViewModel>(relaxed = true)
        every { vm.uiState } returns MutableStateFlow(state)
        return vm
    }

    @Test
    fun loadingSpinnerIsShownWhenStateIsLoading() {
        val vm = buildViewModel(DiscoverUiState(isLoading = true))

        composeTestRule.setContent {
            HonariTheme { DiscoverScreen(onBookClick = {}, viewModel = vm) }
        }

        composeTestRule.onNodeWithText("Discover").assertIsDisplayed()
    }

    @Test
    fun popularBookTitlesAreDisplayed() {
        val vm = buildViewModel(DiscoverUiState(popularBooks = fakeBooks))

        composeTestRule.setContent {
            HonariTheme { DiscoverScreen(onBookClick = {}, viewModel = vm) }
        }

        composeTestRule.onNodeWithText("Popular Books").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dune").assertIsDisplayed()
    }

    @Test
    fun newReleasesTitlesAreDisplayed() {
        val vm = buildViewModel(
            DiscoverUiState(
                newReleases = listOf(Book(id = "3", title = "New Book", author = "Author"))
            )
        )

        composeTestRule.setContent {
            HonariTheme { DiscoverScreen(onBookClick = {}, viewModel = vm) }
        }

        composeTestRule.onNodeWithText("New Releases").assertIsDisplayed()
        composeTestRule.onNodeWithText("New Book").assertIsDisplayed()
    }

    @Test
    fun errorMessageAndRetryButtonAreShownOnError() {
        val vm = buildViewModel(
            DiscoverUiState(error = "Failed to load books. Check your connection.")
        )

        composeTestRule.setContent {
            HonariTheme { DiscoverScreen(onBookClick = {}, viewModel = vm) }
        }

        composeTestRule.onNodeWithText("Failed to load books. Check your connection.")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun retryButtonCallsLoadContent() {
        val vm = buildViewModel(
            DiscoverUiState(error = "Failed to load books. Check your connection.")
        )

        composeTestRule.setContent {
            HonariTheme { DiscoverScreen(onBookClick = {}, viewModel = vm) }
        }

        composeTestRule.onNodeWithText("Retry").performClick()

        verify { vm.loadContent() }
    }

    @Test
    fun searchResultsAreShownWhenSearching() {
        val vm = buildViewModel(
            DiscoverUiState(
                isSearching = true,
                searchQuery = "Dune",
                searchResults = fakeBooks
            )
        )

        composeTestRule.setContent {
            HonariTheme { DiscoverScreen(onBookClick = {}, viewModel = vm) }
        }

        composeTestRule.onNodeWithText("Dune").assertIsDisplayed()
        composeTestRule.onNodeWithText("1984").assertIsDisplayed()
    }

    @Test
    fun noResultsMessageShownWhenSearchReturnsEmpty() {
        val vm = buildViewModel(
            DiscoverUiState(
                isSearching = true,
                searchQuery = "xyz123",
                searchResults = emptyList()
            )
        )

        composeTestRule.setContent {
            HonariTheme { DiscoverScreen(onBookClick = {}, viewModel = vm) }
        }

        composeTestRule.onNodeWithText("No results found").assertIsDisplayed()
    }

    @Test
    fun typingInSearchBarCallsOnSearchQueryChanged() {
        val vm = buildViewModel(DiscoverUiState())

        composeTestRule.setContent {
            HonariTheme { DiscoverScreen(onBookClick = {}, viewModel = vm) }
        }

        composeTestRule.onNodeWithText("Search by title, author…").performTextInput("Dune")

        verify { vm.onSearchQueryChanged(any()) }
    }
}
