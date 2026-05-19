package com.honari.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Auth : Screen("auth")
    data object Feed : Screen("feed")
    data object Scanner : Screen("scanner")
    data object Library : Screen("library")
    data object Profile : Screen("profile")
    data object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: String) = "book_detail/$bookId"
    }
}
