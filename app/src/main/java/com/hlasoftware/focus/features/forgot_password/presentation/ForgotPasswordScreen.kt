package com.hlasoftware.focus.features.forgot_password.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

// Importación para el ícono de la flecha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.hlasoftware.focus.R

@Composable
fun ForgotPasswordScreen(
    vm: ForgotPasswordViewModel = koinViewModel(),
    // Agregamos este callback para la navegación de regreso
    onBackClicked: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val uiState by vm.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Agregamos el botón de regreso y el título
        // Usamos un Box para superponer los elementos
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.forgot_password_back_button)
                )
            }
        }

        // Agregamos el título de la pantalla
        Text(
            text = stringResource(id = R.string.forgot_password_title),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(id = R.string.forgot_password_email_label)) }
        )

        Button(
            onClick = { /* Lógica para recuperar contraseña */ },
            modifier = Modifier.padding(top = 16.dp),
            enabled = uiState !is ForgotPasswordUiState.Loading
        ) {
            if (uiState is ForgotPasswordUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(stringResource(id = R.string.forgot_password_recover_button))
            }
        }

        when (uiState) {
            is ForgotPasswordUiState.Success -> {
                Text(
                    text = stringResource(id = R.string.forgot_password_success_message),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            is ForgotPasswordUiState.Error -> {
                Text(
                    text = (uiState as ForgotPasswordUiState.Error).message,
                    modifier = Modifier.padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {}
        }
    }
}