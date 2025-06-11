package com.honari.app.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Exposes the persisted [ThemePreference] and lets any screen change it.
 * Lives in the Activity scope so every screen sees the same instance.
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(private val userPreferences: UserPreferences) :
    ViewModel() {

    val themePreference: StateFlow<ThemePreference> = userPreferences.themeFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemePreference.SYSTEM)

    fun setTheme(theme: ThemePreference) {
        viewModelScope.launch { userPreferences.setTheme(theme) }
    }
}
