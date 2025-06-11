package com.honari.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.honari.app.domain.repository.AuthRepository
import com.honari.app.presentation.navigation.HonariNavHost
import com.honari.app.presentation.screens.auth.AuthViewModel
import com.honari.app.presentation.screens.book.BookDetailViewModel
import com.honari.app.presentation.screens.discover.DiscoverViewModel
import com.honari.app.presentation.screens.library.LibraryViewModel
import com.honari.app.presentation.screens.profile.ProfileViewModel
import com.honari.app.presentation.screens.scan.ScanViewModel
import com.honari.app.presentation.theme.HonariTheme
import com.honari.app.presentation.theme.ThemePreference
import com.honari.app.presentation.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main activity that hosts the entire app.
 * Uses Compose for UI and Hilt for dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    private val authViewModel: AuthViewModel by viewModels()
    private val discoverViewModel: DiscoverViewModel by viewModels()
    private val libraryViewModel: LibraryViewModel by viewModels()
    private val scanViewModel: ScanViewModel by viewModels()
    private val bookDetailViewModel: BookDetailViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themePreference by themeViewModel.themePreference.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val useDark = when (themePreference) {
                ThemePreference.DARK -> true
                ThemePreference.LIGHT -> false
                ThemePreference.SYSTEM -> systemDark
            }

            HonariTheme(darkTheme = useDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HonariNavHost(
                        authRepository = authRepository,
                        authViewModel = authViewModel,
                        discoverViewModel = discoverViewModel,
                        libraryViewModel = libraryViewModel,
                        scanViewModel = scanViewModel,
                        bookDetailViewModel = bookDetailViewModel,
                        profileViewModel = profileViewModel,
                        themePreference = themePreference,
                        onThemeChange = themeViewModel::setTheme
                    )
                }
            }
        }
    }
}
