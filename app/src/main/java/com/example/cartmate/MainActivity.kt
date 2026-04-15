package com.example.cartmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cartmate.navigation.AppNavigation
import com.example.cartmate.ui.theme.CartMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CartMateTheme {
                AppNavigation()
            }
        }
    }
}