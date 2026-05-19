package com.honari.app

import androidx.lifecycle.ViewModel
import com.honari.app.data.local.preferences.PreferencesDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val preferences: PreferencesDataSource,
) : ViewModel() {

    private val _isDarkMode = MutableStateFlow(preferences.isDarkMode)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        preferences.isDarkMode = enabled
        _isDarkMode.value = enabled
    }
}
