package com.holamundo.agoralist.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.holamundo.agoralist.ui.components.ScreenTopBar

@Composable
fun TermsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            ScreenTopBar(
                title = "Términos y Condiciones",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    loadUrl("file:///android_asset/terms.html")
                }
            }
        )
    }
}
