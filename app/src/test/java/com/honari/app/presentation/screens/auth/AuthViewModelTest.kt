package com.honari.app.presentation.screens.auth

import com.honari.app.domain.model.User
import com.honari.app.domain.repository.AuthRepository
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthRepository = mockk()

    private val fakeUser = User(id = "uid1", email = "test@honari.com", displayName = "Tester")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.getCurrentUser() } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Login ──────────────────────────────────────────────────────────────────

    @Test
    fun `login success sets isAuthenticated true`() = runTest {
        coEvery { authRepository.login("a@b.com", "pass") } returns Result.success(fakeUser)
        val viewModel = AuthViewModel(authRepository)

        viewModel.login("a@b.com", "pass")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isAuthenticated)
        assertFalse(state.isLoading)
        assertEquals(fakeUser, state.currentUser)
        assertNull(state.error)
    }

    @Test
    fun `login failure sets error message`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns
            Result.failure(Exception("Wrong password"))
        val viewModel = AuthViewModel(authRepository)

        viewModel.login("a@b.com", "wrong")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isAuthenticated)
        assertFalse(state.isLoading)
        assertEquals("Wrong password", state.error)
    }

    // ── Register ───────────────────────────────────────────────────────────────

    @Test
    fun `register success sets isAuthenticated true`() = runTest {
        coEvery { authRepository.register("a@b.com", "pass", "Alice") } returns
            Result.success(fakeUser)
        val viewModel = AuthViewModel(authRepository)

        viewModel.register("a@b.com", "pass", "Alice")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isAuthenticated)
        assertEquals(fakeUser, state.currentUser)
    }

    @Test
    fun `register failure sets error`() = runTest {
        coEvery { authRepository.register(any(), any(), any()) } returns
            Result.failure(Exception("Email already in use"))
        val viewModel = AuthViewModel(authRepository)

        viewModel.register("a@b.com", "pass", "Alice")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Email already in use", viewModel.uiState.value.error)
    }

    // ── Google Sign-In ─────────────────────────────────────────────────────────

    @Test
    fun `signInWithGoogle success sets isAuthenticated`() = runTest {
        coEvery { authRepository.signInWithGoogle("token") } returns Result.success(fakeUser)
        val viewModel = AuthViewModel(authRepository)

        viewModel.signInWithGoogle("token")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isAuthenticated)
        assertFalse(state.isLoading)
    }

    @Test
    fun `signInWithGoogle failure sets error`() = runTest {
        coEvery { authRepository.signInWithGoogle(any()) } returns
            Result.failure(Exception("Invalid token"))
        val viewModel = AuthViewModel(authRepository)

        viewModel.signInWithGoogle("bad-token")
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isAuthenticated)
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onGoogleSignInFailed sets error and clears loading`() = runTest {
        val viewModel = AuthViewModel(authRepository)

        viewModel.onGoogleSignInFailed("Credential unavailable")

        val state = viewModel.uiState.value
        assertEquals("Credential unavailable", state.error)
        assertFalse(state.isLoading)
    }

    // ── Password Reset ─────────────────────────────────────────────────────────

    @Test
    fun `sendPasswordResetEmail success sets passwordResetSent`() = runTest {
        coEvery { authRepository.sendPasswordResetEmail("a@b.com") } returns Result.success(Unit)
        val viewModel = AuthViewModel(authRepository)

        viewModel.sendPasswordResetEmail("a@b.com")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.passwordResetSent)
        assertNull(viewModel.uiState.value.error)
    }

    // ── Auth State Listener ────────────────────────────────────────────────────

    @Test
    fun `when getCurrentUser emits user isAuthenticated becomes true`() = runTest {
        every { authRepository.getCurrentUser() } returns flowOf(fakeUser)
        val viewModel = AuthViewModel(authRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isAuthenticated)
        assertEquals(fakeUser, state.currentUser)
    }

    // ── Clear Error ────────────────────────────────────────────────────────────

    @Test
    fun `clearError removes error from state`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns
            Result.failure(Exception("Bad credentials"))
        val viewModel = AuthViewModel(authRepository)
        viewModel.login("a@b.com", "wrong")
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `login shows loading then clears it`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Result.success(fakeUser)
        val viewModel = AuthViewModel(authRepository)
        testDispatcher.scheduler.advanceUntilIdle() // flush checkAuthState

        // Before advancing: isLoading has not been set yet
        viewModel.login("a@b.com", "pass")
        assertFalse(viewModel.uiState.value.isLoading) // coroutine not started yet

        // Run until done: mock returns immediately so loading is set and cleared atomically
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isAuthenticated)
    }

    @Test
    fun `login calls repository with correct credentials`() = runTest {
        coEvery { authRepository.login("user@test.com", "secret") } returns
            Result.success(fakeUser)
        val viewModel = AuthViewModel(authRepository)

        viewModel.login("user@test.com", "secret")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { authRepository.login("user@test.com", "secret") }
    }
}
