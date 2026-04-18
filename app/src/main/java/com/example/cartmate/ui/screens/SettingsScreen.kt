package com.example.cartmate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartmate.ui.components.BottomNavigationBar
import com.example.cartmate.ui.viewmodel.SettingsViewModel
import com.example.cartmate.ui.viewmodel.ThemeViewModel
import com.example.cartmate.ui.viewmodel.ViewModelProvider

private val SettingsCardShape = RoundedCornerShape(16.dp)

@Composable
fun SettingsScreen(navController: NavController) {

    val context = LocalContext.current

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = ViewModelProvider.provideSettingsViewModelFactory(context)
    )

    val themeViewModel: ThemeViewModel = viewModel(
        factory = ViewModelProvider.provideThemeViewModelFactory(context)
    )

    val settingsState by settingsViewModel.uiState.collectAsState()
    val themeState by themeViewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = "settings"
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {

            Text(
                text = "Configuración",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordChangeCard(
                newPassword = settingsState.newPassword,
                isSaving = settingsState.isSavingPassword,
                successMessage = settingsState.passwordSuccessMessage,
                errorMessage = settingsState.errorMessage,
                onPasswordChange = settingsViewModel::onPasswordChange,
                onChangePasswordClick = settingsViewModel::changePassword
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsSwitchCard(
                title = "Modo oscuro",
                checked = themeState.isDarkModeEnabled,
                onCheckedChange = themeViewModel::setDarkModeEnabled
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    settingsViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Cerrar Sesión",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
private fun PasswordChangeCard(
    newPassword: String,
    isSaving: Boolean,
    successMessage: String?,
    errorMessage: String?,
    onPasswordChange: (String) -> Unit,
    onChangePasswordClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = SettingsCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Cambiar contraseña",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nueva contraseña") },
                singleLine = true
            )

            if (successMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = successMessage,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onChangePasswordClick,
                enabled = !isSaving,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = if (isSaving) "Guardando..." else "Actualizar contraseña",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun SettingsSwitchCard(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = SettingsCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}