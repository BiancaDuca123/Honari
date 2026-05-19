package com.honari.app.presentation.screens.auth

import app.cash.turbine.test
import com.honari.app.domain.model.User
import com.honari.app.domain.repository.AuthRepository
import io.mockk.coEvery
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository = mockk<AuthRepository>()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.getCurrentUser() } returns flowOf(null)
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `initial state is not authenticated`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isAuthenticated)
            assertNull(state.currentUser)
            assertFalse(state.isLoading)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when current user exists, state is authenticated`() = runTest {
        val user = User(id = "123", email = "test@example.com", displayName = "Test User")
        every { authRepository.getCurrentUser() } returns flowOf(user)
        viewModel = AuthViewModel(authRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isAuthenticated)
            assertEquals(user, state.currentUser)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `signInWithGoogle success updates state to authenticated`() = runTest {
        val user = User(id = "123", email = "test@example.com", displayName = "Test User")
        coEvery { authRepository.signInWithGoogle(any()) } returns Result.success(user)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.signInWithGoogle("token")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isAuthenticated)
            assertFalse(state.isLoading)
            assertNull(state.error)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `signInWithGoogle failure sets error state`() = runTest {
        coEvery { authRepository.signInWithGoogle(any()) } returns
            Result.failure(Exception("Network error"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.signInWithGoogle("invalid_token")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isAuthenticated)
            assertEquals("Network error", state.error)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `logout clears auth state`() = runTest {
        coEvery { authRepository.logout() } returns Unit
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isAuthenticated)
            assertNull(state.currentUser)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        coEvery { authRepository.signInWithGoogle(any()) } returns
            Result.failure(Exception("Error"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.signInWithGoogle("token")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertNull(state.error)
            cancelAndConsumeRemainingEvents()
        }
    }
}
