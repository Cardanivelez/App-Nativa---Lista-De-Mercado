package com.example.cartmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cartmate.data.repository.ThemeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** [isDarkModeEnabled] por defecto claro; solo true si el usuario lo guardó en DataStore. */
data class ThemeUiState(
    val isDarkModeEnabled: Boolean = false,
    val isLoaded: Boolean = false
)

class ThemeViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {
    val uiState: StateFlow<ThemeUiState> = themeRepository.isDarkModeEnabled.map { isDarkMode ->
        ThemeUiState(
            isDarkModeEnabled = isDarkMode,
            isLoaded = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ThemeUiState(isDarkModeEnabled = false, isLoaded = false)
    )

    fun setDarkModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            themeRepository.setDarkModeEnabled(enabled)
        }
    }

    class Factory(
        private val themeRepository: ThemeRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
                return ThemeViewModel(themeRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
