package com.honari.app.presentation.navigation

import androidx.lifecycle.ViewModel
import com.honari.app.data.local.preferences.PreferencesDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    private val preferences: PreferencesDataSource,
) : ViewModel() {
    val isOnboardingDone: Boolean
        get() = preferences.isOnboardingDone

    fun markOnboardingDone() {
        preferences.isOnboardingDone = true
    }
}
