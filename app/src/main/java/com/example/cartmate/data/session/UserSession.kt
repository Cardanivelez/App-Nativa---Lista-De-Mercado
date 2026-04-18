package com.example.cartmate.data.session

import com.example.cartmate.data.local.entity.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserSession {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    fun setCurrentUser(user: UserEntity) {
        _currentUser.value = user
    }

    fun clear() {
        _currentUser.value = null
    }
}
