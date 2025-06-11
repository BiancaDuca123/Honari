package com.honari.app.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavController
import com.honari.app.presentation.screens.auth.AuthUiState
import com.honari.app.presentation.screens.auth.AuthViewModel
import com.honari.app.presentation.screens.auth.LoginScreen
import com.honari.app.presentation.theme.HonariTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun buildViewModel(state: AuthUiState = AuthUiState()): AuthViewModel {
        val vm = mockk<AuthViewModel>(relaxed = true)
        every { vm.uiState } returns MutableStateFlow(state)
        return vm
    }

    @Test
    fun loginScreenRendersCorrectly() {
        composeTestRule.setContent {
            HonariTheme {
                LoginScreen(
                    navController = mockk(relaxed = true),
                    viewModel = buildViewModel()
                )
            }
        }

        composeTestRule.onNodeWithText("Welcome back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign in").assertIsDisplayed()
        composeTestRule.onNodeWithText("Continue with Google").assertIsDisplayed()
    }

    @Test
    fun signInButtonIsDisabledWhenFieldsAreEmpty() {
        composeTestRule.setContent {
            HonariTheme {
                LoginScreen(
                    navController = mockk(relaxed = true),
                    viewModel = buildViewModel()
                )
            }
        }

        composeTestRule.onNodeWithText("Sign in").assertIsNotEnabled()
    }

    @Test
    fun signInButtonIsEnabledAfterEmailAndPasswordEntered() {
        composeTestRule.setContent {
            HonariTheme {
                LoginScreen(
                    navController = mockk(relaxed = true),
                    viewModel = buildViewModel()
                )
            }
        }

        composeTestRule.onNodeWithText("Email").performTextInput("test@honari.com")
        composeTestRule.onNodeWithText("Password").performTextInput("secret123")

        composeTestRule.onNodeWithText("Sign in").assertIsEnabled()
    }

    @Test
    fun clickingSignInCallsViewModelLogin() {
        val vm = buildViewModel()
        composeTestRule.setContent {
            HonariTheme {
                LoginScreen(navController = mockk(relaxed = true), viewModel = vm)
            }
        }

        composeTestRule.onNodeWithText("Email").performTextInput("test@honari.com")
        composeTestRule.onNodeWithText("Password").performTextInput("secret123")
        composeTestRule.onNodeWithText("Sign in").performClick()

        verify { vm.login("test@honari.com", "secret123") }
    }

    @Test
    fun loadingIndicatorShownWhileLoading() {
        composeTestRule.setContent {
            HonariTheme {
                LoginScreen(
                    navController = mockk(relaxed = true),
                    viewModel = buildViewModel(AuthUiState(isLoading = true))
                )
            }
        }

        // Button should be disabled during loading
        composeTestRule.onNodeWithText("Sign in").assertIsNotEnabled()
    }

    @Test
    fun errorSnackbarIsShownWhenErrorPresent() {
        composeTestRule.setContent {
            HonariTheme {
                LoginScreen(
                    navController = mockk(relaxed = true),
                    viewModel = buildViewModel(AuthUiState(error = "Wrong password"))
                )
            }
        }

        composeTestRule.onNodeWithText("Wrong password").assertIsDisplayed()
    }

    @Test
    fun forgotPasswordNavigatesOnClick() {
        val navController = mockk<NavController>(relaxed = true)
        composeTestRule.setContent {
            HonariTheme {
                LoginScreen(navController = navController, viewModel = buildViewModel())
            }
        }

        composeTestRule.onNodeWithText("Forgot password?").performClick()

        verify { navController.navigate(any<String>()) }
    }
}
