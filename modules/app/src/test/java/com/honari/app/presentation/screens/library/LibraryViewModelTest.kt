package com.honari.app.presentation.screens.library

import app.cash.turbine.test
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.repository.LibraryRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val libraryRepository = mockk<LibraryRepository>()

    private val testBooks = listOf(
        Book(
            id = "1",
            title = "Clean Code",
            authors = listOf("Robert Martin"),
            libraryStatus = ReadingStatus.READ,
            addedAt = 0L,
        ),
        Book(
            id = "2",
            title = "Kotlin in Action",
            authors = listOf("Dmitry Jemerov"),
            libraryStatus = ReadingStatus.WANT_TO_READ,
            addedAt = 0L,
        ),
    )

    private lateinit var viewModel: LibraryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { libraryRepository.getAllBooks() } returns flowOf(testBooks)
        viewModel = LibraryViewModel(libraryRepository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `initial state has no filter selected and empty displayed books`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.selectedFilter)
            assertEquals(0, state.displayedBooks.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `selecting CONTINUE_READING filter shows only READ books`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectFilter(LibraryFilter.CONTINUE_READING)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(LibraryFilter.CONTINUE_READING, state.selectedFilter)
            assertEquals(1, state.displayedBooks.size)
            assertEquals("Clean Code", state.displayedBooks.first().title)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `selecting WISH_LIST filter shows only WANT_TO_READ books`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectFilter(LibraryFilter.WISH_LIST)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(LibraryFilter.WISH_LIST, state.selectedFilter)
            assertEquals(1, state.displayedBooks.size)
            assertEquals("Kotlin in Action", state.displayedBooks.first().title)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `selecting ALL_BOOKS filter shows all books`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectFilter(LibraryFilter.ALL_BOOKS)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(LibraryFilter.ALL_BOOKS, state.selectedFilter)
            assertEquals(2, state.displayedBooks.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `selecting same filter again deselects it`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectFilter(LibraryFilter.WISH_LIST)
        viewModel.selectFilter(LibraryFilter.WISH_LIST)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.selectedFilter)
            cancelAndConsumeRemainingEvents()
        }
    }
}
