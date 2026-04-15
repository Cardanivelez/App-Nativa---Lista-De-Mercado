package com.example.cartmate.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

private data class BottomTab(
    val label: String,
    val route: String
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String
) {
    val tabs = listOf(
        BottomTab("Inicio", "home"),
        BottomTab("Perfil", "profile"),
        BottomTab("Configuración", "settings"),
        BottomTab("Créditos", "credits")
    )

    NavigationBar {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = {
                    if (currentRoute != tab.route) {
                        navController.navigate(tab.route) {
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Box(modifier = Modifier.size(0.dp)) },
                label = { Text(text = tab.label) }
            )
        }
    }
}
