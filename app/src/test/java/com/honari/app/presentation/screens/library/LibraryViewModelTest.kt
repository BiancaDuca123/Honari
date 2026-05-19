package com.honari.app.presentation.screens.library

import app.cash.turbine.test
import com.honari.app.domain.model.Book
import com.honari.app.domain.model.ReadingStatus
import com.honari.app.domain.repository.LibraryRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
    fun `initial state shows READ tab with filtered books`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(ReadingStatus.READ, state.selectedTab)
            assertEquals(1, state.displayedBooks.size)
            assertEquals("Clean Code", state.displayedBooks.first().title)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `selecting WANT_TO_READ tab filters books correctly`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.selectTab(ReadingStatus.WANT_TO_READ)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(ReadingStatus.WANT_TO_READ, state.selectedTab)
            assertEquals(1, state.displayedBooks.size)
            assertEquals("Kotlin in Action", state.displayedBooks.first().title)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `removeBook calls repository with correct id`() = runTest {
        coEvery { libraryRepository.removeBook(any()) } returns Unit
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.removeBook("1")
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { libraryRepository.removeBook("1") }
    }

    @Test
    fun `moveBook calls updateStatus with correct params`() = runTest {
        coEvery { libraryRepository.updateStatus(any(), any()) } returns Unit
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.moveBook("1", ReadingStatus.WANT_TO_READ)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { libraryRepository.updateStatus("1", ReadingStatus.WANT_TO_READ) }
    }
}
