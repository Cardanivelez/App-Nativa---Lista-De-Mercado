package com.holamundo.agoralist.data.local.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class ThemePreferences(
    private val context: Context
) {
    /** Ausente o null → modo claro (no oscuro) hasta que el usuario active oscuro. */
    private val darkModeKey = booleanPreferencesKey("dark_mode_enabled")

    val isDarkModeEnabled: Flow<Boolean> = context.dataStore.data.map { prefs: Preferences ->
        prefs[darkModeKey] ?: false
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[darkModeKey] = enabled
        }
    }
}
