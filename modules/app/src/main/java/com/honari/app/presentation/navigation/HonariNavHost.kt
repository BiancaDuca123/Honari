package com.honari.app.presentation.navigation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.honari.app.presentation.screens.auth.AuthScreen
import com.honari.app.presentation.screens.auth.AuthViewModel
import com.honari.app.presentation.screens.bookdetail.BookDetailScreen
import com.honari.app.presentation.screens.feed.FeedScreen
import com.honari.app.presentation.screens.library.LibraryScreen
import com.honari.app.presentation.screens.onboarding.OnboardingScreen
import com.honari.app.presentation.screens.profile.ProfileScreen
import com.honari.app.presentation.screens.scanner.ScannerScreen
import com.honari.app.presentation.screens.splash.SplashScreen
import com.honari.app.presentation.theme.BackgroundBeige
import com.honari.app.presentation.theme.BrownHeadline
import com.honari.app.presentation.theme.TextSecondary

private const val PREFERENCES_NAME = "honari_preferences"
private const val ONBOARDING_KEY = "onboarding_done"
private const val BOOK_ID_ARGUMENT = "bookId"
private const val BOTTOM_BAR_INDICATOR_WIDTH = 0.46f

private data class NavItem(val screen: Screen, val icon: ImageVector, val label: String)

private val navItems = listOf(
    NavItem(Screen.Library, Icons.Default.CollectionsBookmark, "My Collection"),
    NavItem(Screen.Feed, Icons.Default.AutoStories, "Explore"),
    NavItem(Screen.Profile, Icons.Default.Person, "My Profile"),
)
private val bottomNavRoutes = navItems.map { it.screen.route }.toSet()
private val publicRoutes = setOf(Screen.Splash.route, Screen.Onboarding.route, Screen.Auth.route)

@Composable
fun HonariNavHost() {
    val context = LocalContext.current
    val preferences = remember {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
    var onboardingDone by remember {
        mutableStateOf(preferences.getBoolean(ONBOARDING_KEY, false))
    }
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route

    LaunchedEffect(authState.isAuthenticated, currentRoute) {
        val shouldNavigateToAuth = !authState.isAuthenticated &&
            currentRoute != null &&
            currentRoute !in publicRoutes
        if (shouldNavigateToAuth) {
            navController.navigate(Screen.Auth.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (authState.isAuthenticated && currentRoute in bottomNavRoutes) {
                HonariBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
        containerColor = BackgroundBeige,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onFinished = {
                        val nextRoute = when {
                            !onboardingDone -> Screen.Onboarding.route
                            authState.isAuthenticated -> Screen.Library.route
                            else -> Screen.Auth.route
                        }
                        navController.navigate(nextRoute) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinished = {
                        preferences.edit().putBoolean(ONBOARDING_KEY, true).apply()
                        onboardingDone = true
                        val nextRoute = if (authState.isAuthenticated) {
                            Screen.Library.route
                        } else {
                            Screen.Auth.route
                        }
                        navController.navigate(nextRoute) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(Screen.Auth.route) {
                LaunchedEffect(authState.isAuthenticated) {
                    if (authState.isAuthenticated) {
                        navController.navigate(Screen.Library.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    }
                }
                AuthScreen()
            }
            composable(Screen.Library.route) {
                LibraryScreen(onScanBook = { navController.navigate(Screen.Scanner.route) })
            }
            composable(Screen.Feed.route) {
                FeedScreen(
                    onBookClick = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    },
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
            composable(Screen.Scanner.route) {
                ScannerScreen()
            }
            composable(
                route = Screen.BookDetail.route,
                arguments = listOf(navArgument(BOOK_ID_ARGUMENT) { type = NavType.StringType }),
            ) { backStackEntry ->
                BookDetailScreen(
                    bookId = backStackEntry.arguments?.getString(BOOK_ID_ARGUMENT).orEmpty(),
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
private fun HonariBottomBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            navItems.forEach { item ->
                val selected = currentRoute == item.screen.route
                BottomBarItem(
                    item = item,
                    selected = selected,
                    onClick = { onNavigate(item.screen.route) },
                )
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.BottomBarItem(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (selected) BrownHeadline else TextSecondary,
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) BrownHeadline else TextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .height(3.dp)
                .fillMaxWidth(BOTTOM_BAR_INDICATOR_WIDTH)
                .background(
                    color = if (selected) BrownHeadline else BackgroundBeige,
                    shape = RoundedCornerShape(999.dp),
                ),
        )
    }
}
