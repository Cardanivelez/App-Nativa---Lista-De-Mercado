package com.example.cartmate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cartmate.ui.screens.AddProductScreen
import com.example.cartmate.ui.screens.CreditsScreen
import com.example.cartmate.ui.screens.DetailScreen
import com.example.cartmate.ui.screens.EditProfileScreen
import com.example.cartmate.ui.screens.HomeScreen
import com.example.cartmate.ui.screens.LoginScreen
import com.example.cartmate.ui.screens.ProfileScreen
import com.example.cartmate.ui.screens.RegisterScreen
import com.example.cartmate.ui.screens.SettingsScreen

object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val DETAIL = "detail"
    const val ADD_PRODUCT = "addProduct"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "editProfile"
    const val SETTINGS = "settings"
    const val CREDITS = "credits"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoutes.LOGIN) {

        composable(AppRoutes.LOGIN) {
            LoginScreen(navController)
        }

        composable(AppRoutes.REGISTER) {
            RegisterScreen(navController)
        }

        composable(AppRoutes.HOME) {
            HomeScreen(navController)
        }

        composable(AppRoutes.DETAIL) {
            DetailScreen(navController)
        }

        composable(AppRoutes.ADD_PRODUCT) {
            AddProductScreen(navController)
        }

        composable(AppRoutes.PROFILE) {
            ProfileScreen(navController)
        }

        composable(AppRoutes.EDIT_PROFILE) {
            EditProfileScreen(navController)
        }

        composable(AppRoutes.SETTINGS) {
            SettingsScreen(navController)
        }

        composable(AppRoutes.CREDITS) {
            CreditsScreen(navController)
        }
    }
}