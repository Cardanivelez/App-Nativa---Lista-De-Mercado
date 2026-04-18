package com.example.cartmate.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cartmate.data.session.UserSession
import com.example.cartmate.ui.screens.AddProductScreen
import com.example.cartmate.ui.screens.CreditsScreen
import com.example.cartmate.ui.screens.CreateListScreen
import com.example.cartmate.ui.screens.DetailScreen
import com.example.cartmate.ui.screens.HomeScreen
import com.example.cartmate.ui.screens.LoginScreen
import com.example.cartmate.ui.screens.ProfileScreen
import com.example.cartmate.ui.screens.ProductDetailScreen
import com.example.cartmate.ui.screens.RegisterScreen
import com.example.cartmate.ui.screens.SettingsScreen

object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CREATE_LIST = "createList"
    const val DETAIL = "detail/{listId}"

    /** Ruta concreta en el back stack (p. ej. para [popBackStack]). */
    fun detailRoute(listId: Long): String = "detail/$listId"

    const val PRODUCT_DETAIL = "productDetail/{productId}"
    const val ADD_PRODUCT = "addProduct/{listId}/{productId}"

    /** [productId] 0 = nuevo producto; distinto de 0 = editar producto existente */
    fun addProductRoute(listId: Long, productId: Long = 0L): String =
        "addProduct/$listId/$productId"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val CREDITS = "credits"
}

private const val NavAnimMs = 280
private const val NavAnimOutMs = 240

/** Tabs inferiores: sin slide; solo fundido para evitar “pantalla completa” lateral. */
private val bottomTabEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    fadeIn(animationSpec = tween(NavAnimMs), initialAlpha = 0.99f)
}

private val bottomTabExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(animationSpec = tween(NavAnimOutMs), targetAlpha = 0.99f)
}

private val bottomTabPopEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    fadeIn(animationSpec = tween(NavAnimMs), initialAlpha = 0.99f)
}

private val bottomTabPopExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(animationSpec = tween(NavAnimOutMs), targetAlpha = 0.99f)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentUser by UserSession.currentUser.collectAsState()
    val startDestination = if (currentUser != null) AppRoutes.HOME else AppRoutes.LOGIN

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            enterTransition = {
                slideInHorizontally(animationSpec = tween(NavAnimMs)) { fullWidth -> fullWidth }
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(NavAnimOutMs)) { fullWidth -> -fullWidth }
            },
            popEnterTransition = {
                slideInHorizontally(animationSpec = tween(NavAnimMs)) { fullWidth -> -fullWidth }
            },
            popExitTransition = {
                slideOutHorizontally(animationSpec = tween(NavAnimOutMs)) { fullWidth -> fullWidth }
            }
        ) {

            composable(AppRoutes.LOGIN) {
                LoginScreen(navController)
            }

            composable(AppRoutes.REGISTER) {
                RegisterScreen(navController)
            }

            composable(
                route = AppRoutes.HOME,
                enterTransition = bottomTabEnter,
                exitTransition = bottomTabExit,
                popEnterTransition = bottomTabPopEnter,
                popExitTransition = bottomTabPopExit
            ) {
                HomeScreen(navController)
            }

            composable(AppRoutes.CREATE_LIST) {
                CreateListScreen(navController)
            }

            composable(
                route = AppRoutes.DETAIL,
                arguments = listOf(navArgument("listId") { type = NavType.LongType })
            ) { backStackEntry ->
                val listId = backStackEntry.arguments?.getLong("listId") ?: 0L
                DetailScreen(navController, listId = listId)
            }

            composable(
                route = AppRoutes.ADD_PRODUCT,
                arguments = listOf(
                    navArgument("listId") { type = NavType.LongType },
                    navArgument("productId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val listId = backStackEntry.arguments?.getLong("listId") ?: 0L
                val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                AddProductScreen(
                    navController = navController,
                    listId = listId,
                    productId = productId
                )
            }

            composable(
                route = AppRoutes.PRODUCT_DETAIL,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                ProductDetailScreen(navController, productId = productId)
            }

            composable(
                route = AppRoutes.PROFILE,
                enterTransition = bottomTabEnter,
                exitTransition = bottomTabExit,
                popEnterTransition = bottomTabPopEnter,
                popExitTransition = bottomTabPopExit
            ) {
                ProfileScreen(navController)
            }

            composable(
                route = AppRoutes.SETTINGS,
                enterTransition = bottomTabEnter,
                exitTransition = bottomTabExit,
                popEnterTransition = bottomTabPopEnter,
                popExitTransition = bottomTabPopExit
            ) {
                SettingsScreen(navController)
            }

            composable(
                route = AppRoutes.CREDITS,
                enterTransition = bottomTabEnter,
                exitTransition = bottomTabExit,
                popEnterTransition = bottomTabPopEnter,
                popExitTransition = bottomTabPopExit
            ) {
                CreditsScreen(navController)
            }
        }
    }
}
