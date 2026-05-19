package com.honari.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

private data class NavItem(val screen: Screen, val icon: ImageVector, val label: String)

private val navItems = listOf(
    NavItem(Screen.Feed, Icons.Default.MenuBook, "Feed"),
    NavItem(Screen.Scanner, Icons.Default.CameraAlt, "Scan"),
    NavItem(Screen.Library, Icons.Default.CollectionsBookmark, "Library"),
)
private val bottomNavRoutes = navItems.map { it.screen.route }.toSet()

@Composable
fun HonariNavHost() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    if (!authState.isAuthenticated) {
        AuthScreen()
        return
    }

    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                ) {
                    navItems.forEach { item ->
                        val selected = currentRoute == item.screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selected) {
                                        FontWeight.SemiBold
                                    } else {
                                        FontWeight.Normal
                                    },
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = 0.3f,
                                ),
                            ),
                        )
                    }
                }
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
