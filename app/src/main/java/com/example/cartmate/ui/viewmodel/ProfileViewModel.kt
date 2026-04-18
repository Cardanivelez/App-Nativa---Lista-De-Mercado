package com.example.cartmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cartmate.data.local.entity.UserEntity
import com.example.cartmate.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: UserEntity? = null,
    val editName: String = "",
    val editEmail: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isSavingProfileImage: Boolean = false,
    val avatarVariant: Int = 0,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.currentUser.collect { user ->
                _uiState.update {
                    it.copy(
                        user = user,
                        editName = if (it.isEditing) it.editName else (user?.name ?: ""),
                        editEmail = if (it.isEditing) it.editEmail else (user?.email ?: "")
                    )
                }
            }
        }
    }

    fun startEditing() {
        val user = _uiState.value.user ?: return
        _uiState.update {
            it.copy(
                isEditing = true,
                editName = user.name,
                editEmail = user.email,
                errorMessage = null
            )
        }
    }

    fun cancelEditing() {
        val user = _uiState.value.user
        _uiState.update {
            it.copy(
                isEditing = false,
                editName = user?.name ?: "",
                editEmail = user?.email ?: "",
                errorMessage = null
            )
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(editName = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(editEmail = value, errorMessage = null) }
    }

    fun changeAvatarPlaceholder() {
        _uiState.update { it.copy(avatarVariant = (it.avatarVariant + 1) % 4) }
    }

    fun onProfileImageSelected(uri: String?) {
        val userId = _uiState.value.user?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingProfileImage = true, errorMessage = null) }
            val result = userRepository.updateProfileImageFromPicker(userId, uri)
            _uiState.update {
                it.copy(
                    isSavingProfileImage = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun saveProfile() {
        val state = _uiState.value
        val userId = state.user?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = userRepository.updateProfile(userId, state.editName, state.editEmail)
            _uiState.update {
                it.copy(
                    isSaving = false,
                    isEditing = !result.isSuccess,
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
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
