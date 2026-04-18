package com.example.cartmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cartmate.data.repository.UserRepository
import com.example.cartmate.util.EmailValidator
import com.example.cartmate.util.RegisterPasswordValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false
)

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registerSuccess: Boolean = false
) {
    val emailValidationError: String?
        get() = if (email.isNotBlank() && !EmailValidator.isValid(email)) {
            "Ingresa un correo válido (ejemplo: nombre@dominio.com)"
        } else {
            null
        }

    val passwordValidationError: String?
        get() = if (password.isNotBlank() && !RegisterPasswordValidator.isValid(password)) {
            "La contraseña debe tener al menos 4 caracteres, incluyendo una letra y un número"
        } else {
            null
        }

    val confirmPasswordValidationError: String?
        get() = if (confirmPassword.isNotBlank() && confirmPassword != password) {
            "Las contraseñas no coinciden"
        } else {
            null
        }

    val isRegisterFormValid: Boolean
        get() = fullName.isNotBlank() &&
            email.isNotBlank() &&
            EmailValidator.isValid(email) &&
            password.isNotBlank() &&
            RegisterPasswordValidator.isValid(password) &&
            confirmPassword.isNotBlank() &&
            confirmPassword == password
}

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    fun onLoginEmailChange(value: String) {
        _loginUiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onLoginPasswordChange(value: String) {
        _loginUiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun login() {
        val state = _loginUiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _loginUiState.update { it.copy(errorMessage = "Completa email y contraseña") }
            return
        }
        if (!EmailValidator.isValid(state.email)) {
            _loginUiState.update { it.copy(errorMessage = "Correo electrónico inválido") }
            return
        }
        viewModelScope.launch {
            _loginUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = userRepository.login(state.email, state.password)
            _loginUiState.update {
                it.copy(
                    isLoading = false,
                    loginSuccess = result.isSuccess,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun consumeLoginSuccess() {
        _loginUiState.update { it.copy(loginSuccess = false) }
    }

    fun onRegisterNameChange(value: String) {
        _registerUiState.update { it.copy(fullName = value, errorMessage = null) }
    }

    fun onRegisterEmailChange(value: String) {
        _registerUiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onRegisterPasswordChange(value: String) {
        _registerUiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onRegisterConfirmPasswordChange(value: String) {
        _registerUiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun register() {
        val state = _registerUiState.value
        if (!state.isRegisterFormValid) return
        viewModelScope.launch {
            _registerUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = userRepository.registerUser(state.fullName, state.email, state.password)
            _registerUiState.update {
                it.copy(
                    isLoading = false,
                    registerSuccess = result.isSuccess,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun consumeRegisterSuccess() {
        _registerUiState.update { it.copy(registerSuccess = false) }
    }

    class Factory(
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
