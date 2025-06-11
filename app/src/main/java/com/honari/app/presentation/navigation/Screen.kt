package com.honari.app.presentation.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Sealed class representing all top-level navigation destinations.
 * Follows Single Responsibility Principle by encapsulating navigation routes.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Password : Screen("password")
    object Register : Screen("register")
    object MainNav : Screen("main_nav")
    object Discover : Screen("discover")
    object Library : Screen("library")
    object Scan : Screen("scan")
    object Profile : Screen("profile")
    object BookDetail : Screen("book/{bookId}") {
        val arguments = listOf(
            navArgument("bookId") { type = NavType.StringType }
        )

        fun createRoute(bookId: String) = "book/$bookId"
    }
}
