package com.honari.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object Feed : Screen("feed")
    data object Scanner : Screen("scanner")
    data object Library : Screen("library")
    data object Profile : Screen("profile")
    data class BookDetail(val bookId: String = "{bookId}") : Screen("book/{bookId}") {
        fun withId(id: String) = "book/$id"
    }
}
