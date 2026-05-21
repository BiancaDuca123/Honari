package com.honari.app.presentation.screens.feed

import app.cash.turbine.test
import com.honari.app.domain.model.Book
import com.honari.app.domain.repository.BookRepository
import com.honari.app.domain.repository.LibraryRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val bookRepository = mockk<BookRepository>()
    private val libraryRepository = mockk<LibraryRepository>()

    private val testBooks = listOf(
        Book(id = "1", title = "Book One", authors = listOf("Author A")),
        Book(id = "2", title = "Book Two", authors = listOf("Author B")),
    )

    private lateinit var viewModel: FeedViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { libraryRepository.getAllBooks() } returns flowOf(emptyList())
        every { bookRepository.getFeedBooks(any(), any()) } returns flowOf(testBooks)
        viewModel = FeedViewModel(bookRepository, libraryRepository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `initial state loads feed books`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(2, state.books.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `initial load falls back to fiction when library is empty`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        verify { bookRepository.getFeedBooks(query = "subject:fiction", maxResults = any()) }
    }

    @Test
    fun `search returns results after debounce`() = runTest {
        val searchResults = listOf(Book(id = "3", title = "Search Result"))
        coEvery { bookRepository.searchBooks(any()) } returns searchResults
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onSearchQueryChanged("kotlin")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("kotlin", state.searchQuery)
            assertEquals(1, state.searchResults.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `initial load builds personalized query from library books`() = runTest {
        every {
            libraryRepository.getAllBooks()
        } returns flowOf(
            listOf(
                Book(
                    id = "10",
                    authors = listOf("Author A", "Author B"),
                    categories = listOf("Science Fiction", "Fantasy"),
                ),
                Book(
                    id = "11",
                    authors = listOf("Author A", "Author C"),
                    categories = listOf("Mystery", "Fantasy"),
                ),
            ),
        )
        viewModel = FeedViewModel(bookRepository, libraryRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        verify {
            bookRepository.getFeedBooks(
                query = "subject:science+fiction+subject:fantasy+subject:mystery+" +
                    "inauthor:author+a+inauthor:author+b",
                maxResults = any(),
            )
        }
    }

    @Test
    fun `clearSearch resets search state`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onSearchQueryChanged("something")
        viewModel.clearSearch()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.searchQuery.isEmpty())
            assertTrue(state.searchResults.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }
}
