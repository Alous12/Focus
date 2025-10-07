package com.hlasoftware.focus.features.signup.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hlasoftware.focus.features.signup.domain.model.UserProfile

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onBackClicked: () -> Unit,
    onSuccess: (user: UserProfile) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Regístrate",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        // Modifier for text fields
        fun Modifier.textFieldStyle() = this
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)

        // Name Field
        OutlinedTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChanged,
            placeholder = { Text("Ingresa tu nombre completo", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier.textFieldStyle(),
            shape = RoundedCornerShape(8.dp)
        )

        // Birthdate Field
        OutlinedTextField(
            value = uiState.birthdate,
            onValueChange = viewModel::onBirthdateChanged,
            placeholder = { Text("Ingresa tu fecha de nacimiento", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier.textFieldStyle(),
            shape = RoundedCornerShape(8.dp)
        )

        // Email Field
        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChanged,
            placeholder = { Text("Ingresa tu número o correo electrónico", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier.textFieldStyle(),
            shape = RoundedCornerShape(8.dp)
        )

        // Password Field
        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChanged,
            placeholder = { Text("Ingresa tu contraseña", color = Color.Gray) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.textFieldStyle(),
            shape = RoundedCornerShape(8.dp)
        )

        // Confirm Password Field
        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChanged,
            placeholder = { Text("Confirma tu contraseña", color = Color.Gray) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.textFieldStyle(),
            shape = RoundedCornerShape(8.dp)
        )

        // Show Error Message
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Show Success Message
        if (uiState.success) {
            Text(
                text = "¡Registro exitoso!",
                color = Color.Green,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Register Button
        Button(
            onClick = {
                viewModel.onSignUpClick()
            },
            enabled = !uiState.loading && uiState.name.isNotBlank() && uiState.email.isNotBlank() && uiState.password.isNotBlank() && uiState.confirmPassword.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (uiState.loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Regístrate", fontSize = 16.sp, color = Color.White)
            }
        }

        Text(
            text = "ó regístrate con",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Social media icons (placeholders for now)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.size(40.dp).background(Color.Gray)) // Facebook
            Box(modifier = Modifier.size(40.dp).background(Color.Gray)) // Google
            Box(modifier = Modifier.size(40.dp).background(Color.Gray)) // Instagram
            Box(modifier = Modifier.size(40.dp).background(Color.Gray)) // Apple
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¿Ya tienes una cuenta? Inicia Sesión",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onBackClicked() }
        )
    }

    // Callback onSuccess when registration is successful
    if (uiState.success) {
        onSuccess(uiState.user!!)
    }
}

