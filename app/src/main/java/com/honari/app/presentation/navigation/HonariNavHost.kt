package com.honari.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.honari.app.domain.repository.AuthRepository
import com.honari.app.presentation.screens.auth.AuthViewModel
import com.honari.app.presentation.screens.auth.LoginScreen
import com.honari.app.presentation.screens.auth.PasswordScreen
import com.honari.app.presentation.screens.auth.RegisterScreen
import com.honari.app.presentation.screens.book.BookDetailScreen
import com.honari.app.presentation.screens.book.BookDetailViewModel
import com.honari.app.presentation.screens.discover.DiscoverScreen
import com.honari.app.presentation.screens.discover.DiscoverViewModel
import com.honari.app.presentation.screens.library.LibraryScreen
import com.honari.app.presentation.screens.library.LibraryViewModel
import com.honari.app.presentation.screens.profile.ProfileScreen
import com.honari.app.presentation.screens.profile.ProfileViewModel
import com.honari.app.presentation.screens.scan.ScanScreen
import com.honari.app.presentation.screens.scan.ScanViewModel
import com.honari.app.presentation.theme.ThemePreference

@Composable
fun HonariNavHost(
    navController: NavHostController = rememberNavController(),
    authRepository: AuthRepository,
    authViewModel: AuthViewModel,
    discoverViewModel: DiscoverViewModel,
    libraryViewModel: LibraryViewModel,
    scanViewModel: ScanViewModel,
    bookDetailViewModel: BookDetailViewModel,
    profileViewModel: ProfileViewModel,
    themePreference: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit
) {
    val startDestination = remember {
        if (authRepository.isLoggedIn()) Screen.MainNav.route else Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController, viewModel = authViewModel)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController, viewModel = authViewModel)
        }
        composable(Screen.Password.route) {
            PasswordScreen(navController = navController, viewModel = authViewModel)
        }
        composable(Screen.MainNav.route) {
            MainNavigationScreen(
                onNavigateToBook = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                discoverViewModel = discoverViewModel,
                libraryViewModel = libraryViewModel,
                scanViewModel = scanViewModel,
                profileViewModel = profileViewModel,
                themePreference = themePreference,
                onThemeChange = onThemeChange
            )
        }
        composable(Screen.BookDetail.route, Screen.BookDetail.arguments) { backStack ->
            val bookId = backStack.arguments?.getString("bookId") ?: ""
            BookDetailScreen(
                bookId = bookId,
                onBack = { navController.popBackStack() },
                viewModel = bookDetailViewModel
            )
        }
    }
}

@Composable
fun MainNavigationScreen(
    onNavigateToBook: (String) -> Unit,
    onSignOut: () -> Unit,
    discoverViewModel: DiscoverViewModel,
    libraryViewModel: LibraryViewModel,
    scanViewModel: ScanViewModel,
    profileViewModel: ProfileViewModel,
    themePreference: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit
) {
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val isScanActive = navBackStackEntry?.destination?.route == Screen.Scan.route

    Scaffold(
        bottomBar = { if (!isScanActive) BottomNavigationBar(navController = innerNavController) }
    ) { innerPadding ->
        val mod = if (isScanActive) Modifier else Modifier.padding(innerPadding)
        Box(modifier = mod) {
            NavHost(navController = innerNavController, startDestination = Screen.Discover.route) {
                composable(Screen.Discover.route) {
                    DiscoverScreen(onBookClick = onNavigateToBook, viewModel = discoverViewModel)
                }
                composable(Screen.Library.route) {
                    LibraryScreen(
                        onScanClick = {
                            innerNavController.navigate(Screen.Scan.route) {
                                launchSingleTop =
                                    true
                            }
                        },
                        viewModel = libraryViewModel
                    )
                }
                composable(Screen.Scan.route) {
                    ScanScreen(navController = innerNavController, viewModel = scanViewModel)
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onSignOut = onSignOut,
                        viewModel = profileViewModel,
                        themePreference = themePreference,
                        onThemeChange = onThemeChange
                    )
                }
            }
        }
    }
}
