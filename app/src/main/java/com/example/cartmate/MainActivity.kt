package com.example.cartmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cartmate.navigation.AppNavigation
import com.example.cartmate.ui.theme.CartMateTheme
import com.example.cartmate.ui.viewmodel.ThemeViewModel
import com.example.cartmate.ui.viewmodel.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ViewModelProvider.provideThemeViewModelFactory(context)
            )
            val themeState by themeViewModel.uiState.collectAsState()
            CartMateTheme(darkTheme = themeState.isDarkModeEnabled, dynamicColor = false) {
                if (themeState.isLoaded) {
                    AppNavigation()
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}