package com.example.cartmate.data.repository

import com.example.cartmate.data.local.prefs.ThemePreferences
import kotlinx.coroutines.flow.Flow

class ThemeRepository(
    private val themePreferences: ThemePreferences
) {
    val isDarkModeEnabled: Flow<Boolean> = themePreferences.isDarkModeEnabled

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        themePreferences.setDarkModeEnabled(enabled)
    }
}
