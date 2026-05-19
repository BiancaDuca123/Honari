package com.honari.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.honari.app.presentation.screens.auth.AuthScreen
import com.honari.app.presentation.screens.auth.AuthViewModel
import com.honari.app.presentation.screens.feed.FeedScreen
import com.honari.app.presentation.screens.library.LibraryScreen
import com.honari.app.presentation.screens.profile.ProfileScreen
import com.honari.app.presentation.screens.scanner.ScannerScreen

private val bottomNavScreens = listOf(Screen.Feed, Screen.Scanner, Screen.Library)
private val bottomNavRoutes = bottomNavScreens.map { it.route }.toSet()

@Composable
fun HonariNavHost() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    if (!authState.isAuthenticated) {
        AuthScreen()
        return
    }

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val entry by navController.currentBackStackEntryAsState()
            if (entry?.destination?.route in bottomNavRoutes) {
                HonariBottomBar(
                    screens = bottomNavScreens,
                    currentRoute = entry?.destination?.route,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Feed.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Feed.route) {
                FeedScreen(onNavigateToProfile = { navController.navigate(Screen.Profile.route) })
            }
            composable(Screen.Scanner.route) { ScannerScreen() }
            composable(Screen.Library.route) { LibraryScreen() }
            composable(Screen.Profile.route) {
                ProfileScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
