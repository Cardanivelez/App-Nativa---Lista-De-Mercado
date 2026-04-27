package com.holamundo.agoralist.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimary,
    secondary = SecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimary,
    secondary = SecondaryLight
)

@Composable
fun CartMateTheme(
    /** Por defecto claro: no seguir el tema del sistema salvo que el caller pase otro valor. */
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (darkTheme) DarkColorScheme else LightColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Dejamos que la barra sea transparente (manejado por edge-to-edge)
            // Pero controlamos el color de los elementos (iconos de sistema)
            val insetsController = WindowCompat.getInsetsController(window, view)
            
            // Si darkTheme es true (Modo Oscuro), los iconos deben ser blancos (isAppearanceLightStatusBars = false)
            // Si darkTheme es false (Modo Claro), los iconos deben ser negros (isAppearanceLightStatusBars = true)
            insetsController.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}