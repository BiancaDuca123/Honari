package com.honari.app.presentation.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Which color-scheme the user prefers. */
enum class ThemePreference { SYSTEM, LIGHT, DARK }

/**
 * DataStore wrapper that persists user preferences (theme + reading goal)
 * without needing a Firestore round-trip.
 */
@Singleton
class UserPreferences @Inject constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_preference")
        private val READING_GOAL_KEY = intPreferencesKey("reading_goal")
    }

    val themeFlow: Flow<ThemePreference> = dataStore.data.map { prefs ->
        when (prefs[THEME_KEY]) {
            ThemePreference.LIGHT.name -> ThemePreference.LIGHT
            ThemePreference.DARK.name -> ThemePreference.DARK
            else -> ThemePreference.SYSTEM
        }
    }

    val readingGoalFlow: Flow<Int> = dataStore.data.map { prefs ->
        prefs[READING_GOAL_KEY] ?: 0 // 0 = not set
    }

    suspend fun setTheme(theme: ThemePreference) {
        dataStore.edit { it[THEME_KEY] = theme.name }
    }

    suspend fun setReadingGoal(goal: Int) {
        dataStore.edit { it[READING_GOAL_KEY] = goal.coerceIn(0, 365) }
    }
}
