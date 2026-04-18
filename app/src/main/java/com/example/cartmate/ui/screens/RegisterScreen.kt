package com.example.cartmate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartmate.ui.viewmodel.AuthViewModel
import com.example.cartmate.ui.viewmodel.ViewModelProvider

private val FieldShape = RoundedCornerShape(14.dp)
private val ButtonShape = RoundedCornerShape(14.dp)

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = ViewModelProvider.provideAuthViewModelFactory(context)
    )
    val uiState by authViewModel.registerUiState.collectAsState()

    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            authViewModel.consumeRegisterSuccess()
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RegisterHeader()

            Spacer(modifier = Modifier.height(32.dp))

            RegisterFields(
                fullName = uiState.fullName,
                email = uiState.email,
                password = uiState.password,
                confirmPassword = uiState.confirmPassword,
                emailValidationError = uiState.emailValidationError,
                passwordValidationError = uiState.passwordValidationError,
                confirmPasswordValidationError = uiState.confirmPasswordValidationError,
                onFullNameChange = authViewModel::onRegisterNameChange,
                onEmailChange = authViewModel::onRegisterEmailChange,
                onPasswordChange = authViewModel::onRegisterPasswordChange,
                onConfirmPasswordChange = authViewModel::onRegisterConfirmPasswordChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            RegisterPrimaryButton(
                isLoading = uiState.isLoading,
                enabled = uiState.isRegisterFormValid,
                onClick = authViewModel::register
            )

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "¿Ya tienes cuenta? Inicia sesión",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("login") }
                    .padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RegisterHeader() {
    Text(
        text = "Crear cuenta",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Completa tus datos para comenzar",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun RegisterFields(
    fullName: String,
    email: String,
    password: String,
    confirmPassword: String,
    emailValidationError: String?,
    passwordValidationError: String?,
    confirmPasswordValidationError: String?,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit
) {
    OutlinedTextField(
        value = fullName,
        onValueChange = onFullNameChange,
        modifier = Modifier.fillMaxWidth(),
        shape = FieldShape,
        singleLine = true,
        label = { Text("Nombre completo") },
        placeholder = { Text("Juan Perez") },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        modifier = Modifier.fillMaxWidth(),
        shape = FieldShape,
        singleLine = true,
        label = { Text("Email") },
        placeholder = { Text("tu@email.com") },
        isError = emailValidationError != null,
        supportingText = emailValidationError?.let { msg ->
            {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        modifier = Modifier.fillMaxWidth(),
        shape = FieldShape,
        singleLine = true,
        label = { Text("Contraseña") },
        visualTransformation = PasswordVisualTransformation(),
        isError = passwordValidationError != null,
        supportingText = passwordValidationError?.let { msg ->
            {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        modifier = Modifier.fillMaxWidth(),
        shape = FieldShape,
        singleLine = true,
        label = { Text("Confirmar contraseña") },
        visualTransformation = PasswordVisualTransformation(),
        isError = confirmPasswordValidationError != null,
        supportingText = confirmPasswordValidationError?.let { msg ->
            {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun RegisterPrimaryButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = if (isLoading) "Guardando..." else "Registrarse",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}
