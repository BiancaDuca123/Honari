package com.honari.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.honari.app.presentation.screens.auth.LoginScreen
import com.honari.app.presentation.screens.auth.PasswordScreen
import com.honari.app.presentation.screens.auth.RegisterScreen
import com.honari.app.presentation.screens.book.BookDetailScreen
import com.honari.app.presentation.screens.circles.CirclesScreen
import com.honari.app.presentation.screens.discover.DiscoverScreen
import com.honari.app.presentation.screens.library.LibraryScreen
import com.honari.app.presentation.screens.profile.ProfileScreen
import com.honari.app.presentation.screens.reading.ReadingScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Main navigation host for the Honari app.
 * Manages all screen navigation using Jetpack Navigation Compose.
 */
@Composable
fun HonariNavHost(
    navController: NavHostController = rememberNavController(),
    authRepository: com.honari.app.domain.repository.AuthRepository
) {
    // Check authentication state to determine start destination
    val startDestination = remember {
        runBlocking {
            val user = authRepository.getCurrentUser().first()
            if (user != null) Screen.MainNav.route else Screen.Login.route
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth screens (no bottom nav)
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        composable(Screen.Password.route) {
            PasswordScreen(navController = navController)
        }

        // Main navigation with bottom bar
        composable(Screen.MainNav.route) {
            MainNavigationScreen()
        }

        composable(
            route = Screen.BookDetail.route,
            arguments = Screen.BookDetail.arguments
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            BookDetailScreen(
                bookId = bookId,
                navController = navController
            )
        }
    }
}

/**
 * Main navigation screen with bottom navigation bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigationScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Discover.route
            ) {
                composable(Screen.Discover.route) {
                    DiscoverScreen(navController = navController)
                }

                composable(Screen.Library.route) {
                    LibraryScreen(navController = navController)
                }

                composable(Screen.Reading.route) {
                    ReadingScreen(navController = navController)
                }

                composable(Screen.Circles.route) {
                    CirclesScreen(navController = navController)
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(navController = navController)
                }
            }
        }
    }
}
