package com.example.cartmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cartmate.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val newPassword: String = "",
    val isSavingPassword: Boolean = false,
    val passwordSuccessMessage: String? = null,
    val errorMessage: String? = null
)

class SettingsViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                newPassword = value,
                errorMessage = null,
                passwordSuccessMessage = null
            )
        }
    }

    fun changePassword() {
        val currentUser = userRepository.currentUser.value
        if (currentUser == null) {
            _uiState.update { it.copy(errorMessage = "Sesión no disponible") }
            return
        }
        val newPassword = _uiState.value.newPassword
        if (newPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa una nueva contraseña") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingPassword = true, errorMessage = null) }
            val result = userRepository.changePassword(currentUser.id, newPassword)
            _uiState.update {
                it.copy(
                    isSavingPassword = false,
                    newPassword = if (result.isSuccess) "" else it.newPassword,
                    passwordSuccessMessage = if (result.isSuccess) "Contraseña actualizada correctamente" else null,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun logout() {
        userRepository.logout()
    }

    class Factory(
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
