package com.example.cartmate.ui.viewmodel

import android.content.Context
import com.example.cartmate.data.local.AppDatabase
import com.example.cartmate.data.local.prefs.ThemePreferences
import com.example.cartmate.data.repository.ProductRepository
import com.example.cartmate.data.repository.ShoppingListRepository
import com.example.cartmate.data.repository.ThemeRepository
import com.example.cartmate.data.repository.UserRepository

object ViewModelProvider {
    fun provideAuthViewModelFactory(context: Context): AuthViewModel.Factory {
        val db = AppDatabase.getInstance(context)
        val userRepository = UserRepository(db.userDao(), context.applicationContext)
        return AuthViewModel.Factory(userRepository)
    }

    fun provideHomeViewModelFactory(context: Context): HomeViewModel.Factory {
        val db = AppDatabase.getInstance(context)
        val shoppingListRepository = ShoppingListRepository(db.shoppingListDao())
        return HomeViewModel.Factory(shoppingListRepository)
    }

    fun provideProductViewModelFactory(context: Context): ProductViewModel.Factory {
        val db = AppDatabase.getInstance(context)
        val productRepository = ProductRepository(db.productDao())
        return ProductViewModel.Factory(productRepository)
    }

    fun provideProfileViewModelFactory(context: Context): ProfileViewModel.Factory {
        val db = AppDatabase.getInstance(context)
        val userRepository = UserRepository(db.userDao(), context.applicationContext)
        return ProfileViewModel.Factory(userRepository)
    }

    fun provideSettingsViewModelFactory(context: Context): SettingsViewModel.Factory {
        val db = AppDatabase.getInstance(context)
        val userRepository = UserRepository(db.userDao(), context.applicationContext)
        return SettingsViewModel.Factory(userRepository)
    }

    fun provideThemeViewModelFactory(context: Context): ThemeViewModel.Factory {
        val themeRepository = ThemeRepository(ThemePreferences(context.applicationContext))
        return ThemeViewModel.Factory(themeRepository)
    }
}
