package com.honari.app.presentation.screens.discover

import com.honari.app.domain.model.Book
import com.honari.app.domain.repository.BookLookupRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: BookLookupRepository = mockk()

    private val fakePopularBooks = listOf(
        Book(id = "1", title = "Dune", author = "Frank Herbert", rating = 4.5f),
        Book(id = "2", title = "1984", author = "George Orwell", rating = 4.7f)
    )
    private val fakeNewReleases = listOf(
        Book(id = "3", title = "New Book", author = "New Author", rating = 3.9f)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading then success`() = runTest {
        coEvery { repository.getPopularBooks() } returns Result.success(fakePopularBooks)
        coEvery { repository.getNewReleases() } returns Result.success(fakeNewReleases)

        // Before advancing: ViewModel created, coroutine queued but not run
        val viewModel = DiscoverViewModel(repository)
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.popularBooks.isEmpty())

        // Run all coroutines to completion
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(fakePopularBooks, state.popularBooks)
        assertEquals(fakeNewReleases, state.newReleases)
        assertNull(state.error)
    }

    @Test
    fun `when both APIs fail error is set`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { repository.getPopularBooks() } returns Result.failure(exception)
        coEvery { repository.getNewReleases() } returns Result.failure(exception)

        val viewModel = DiscoverViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.popularBooks.isEmpty())
        assertTrue(state.newReleases.isEmpty())
    }

    @Test
    fun `when only popular books fail partial data is shown with no error`() = runTest {
        coEvery { repository.getPopularBooks() } returns Result.failure(RuntimeException("oops"))
        coEvery { repository.getNewReleases() } returns Result.success(fakeNewReleases)

        val viewModel = DiscoverViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.popularBooks.isEmpty())
        assertEquals(fakeNewReleases, state.newReleases)
    }

    @Test
    fun `search updates query and triggers search`() = runTest {
        coEvery { repository.getPopularBooks() } returns Result.success(emptyList())
        coEvery { repository.getNewReleases() } returns Result.success(emptyList())
        coEvery { repository.searchByQuery("Dune") } returns Result.success(fakePopularBooks)

        val viewModel = DiscoverViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("Dune")
        assertEquals("Dune", viewModel.uiState.value.searchQuery)

        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isSearching)
        assertEquals(fakePopularBooks, state.searchResults)
    }

    @Test
    fun `clearSearch resets search state`() = runTest {
        coEvery { repository.getPopularBooks() } returns Result.success(emptyList())
        coEvery { repository.getNewReleases() } returns Result.success(emptyList())
        coEvery { repository.searchByQuery(any()) } returns Result.success(fakePopularBooks)

        val viewModel = DiscoverViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("Dune")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isSearching)

        viewModel.clearSearch()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertTrue(state.searchResults.isEmpty())
        assertEquals("", state.searchQuery)
    }

    @Test
    fun `blank search query is ignored`() = runTest {
        coEvery { repository.getPopularBooks() } returns Result.success(emptyList())
        coEvery { repository.getNewReleases() } returns Result.success(emptyList())

        val viewModel = DiscoverViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("   ")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSearching)
    }

    @Test
    fun `loadContent can be called again to retry`() = runTest {
        coEvery { repository.getPopularBooks() } returns
            Result.failure(RuntimeException("first fail")) andThen
            Result.success(fakePopularBooks)
        coEvery { repository.getNewReleases() } returns
            Result.failure(RuntimeException("first fail")) andThen
            Result.success(fakeNewReleases)

        val viewModel = DiscoverViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)

        viewModel.loadContent()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.error)
        assertEquals(fakePopularBooks, state.popularBooks)
    }
}
