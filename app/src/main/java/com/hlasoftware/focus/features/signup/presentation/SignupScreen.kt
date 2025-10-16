package com.hlasoftware.focus.features.signup.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hlasoftware.focus.R
import com.hlasoftware.focus.features.signup.domain.model.UserProfile
import com.hlasoftware.focus.ui.theme.AuthBackground
import com.hlasoftware.focus.ui.theme.MidnightGreen
import com.hlasoftware.focus.ui.theme.component.BackgroundShapes // Asumiendo que BackgroundShapes está disponible

// NOTA: Se necesita añadir el composable SocialButton y HorizontalDivider si no están definidos globalmente.
// Los he añadido aquí para que el archivo sea autocontenido.

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onBackClicked: () -> Unit,
    onSuccess: (user: UserProfile) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.error

    // Estados locales para la visibilidad de las contraseñas
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthBackground) // Usar el color de fondo consistente
    ) {
        // CAPA 1: Formas de fondo
        BackgroundShapes(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp), // Padding lateral consistente
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // Espacio para bajar el título "Regístrate"
            Spacer(modifier = Modifier.height(130.dp))

            Text(
                text = "Regístrate",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // --- ESTILO DE CAMPO DE TEXTO DE LÍNEA ---
            val lineTextFieldColors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.LightGray.copy(alpha = 0.5f),
                cursorColor = Color.White
            )
            val lineTextStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp)
            val lineLabelStyle: @Composable (String) -> (@Composable () -> Unit) = { label ->
                { Text(label, color = Color.LightGray.copy(alpha = 0.7f), fontSize = 16.sp) }
            }

            // --- Name Field ---
            TextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                label = lineLabelStyle("Ingresa tu nombre completo"),
                singleLine = true,
                colors = lineTextFieldColors,
                textStyle = lineTextStyle,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Birthdate Field (con icono de calendario) ---
            TextField(
                value = uiState.birthdate,
                onValueChange = viewModel::onBirthdateChanged,
                label = lineLabelStyle("Ingresa tu fecha de nacimiento"),
                singleLine = true,
                colors = lineTextFieldColors,
                textStyle = lineTextStyle,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Seleccionar fecha",
                        tint = Color.LightGray
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Email Field ---
            TextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChanged,
                label = lineLabelStyle("Ingresa tu número o correo electrónico"),
                singleLine = true,
                colors = lineTextFieldColors,
                textStyle = lineTextStyle,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Password Field (con icono de visibilidad) ---
            TextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChanged,
                label = lineLabelStyle("Ingresa tu contraseña"),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector  = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = Color.LightGray
                        )
                    }
                },
                colors = lineTextFieldColors,
                textStyle = lineTextStyle,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Confirm Password Field (con icono de visibilidad) ---
            TextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChanged,
                label = lineLabelStyle("Confirma tu contraseña"),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector  = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle confirm password visibility",
                            tint = Color.LightGray
                        )
                    }
                },
                colors = lineTextFieldColors,
                textStyle = lineTextStyle,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp)) // Espacio antes del botón de registro

            // --- Show Error/Success Message ---
            if (errorMessage != null) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            if (uiState.success) {
                Text("¡Registro exitoso!", color = Color.Green, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }

            // --- Register Button ---
            Button(
                onClick = { viewModel.onSignUpClick() },
                enabled = !uiState.loading && uiState.name.isNotBlank() && uiState.email.isNotBlank() && uiState.password.isNotBlank() && uiState.confirmPassword.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp)), // Altura y bordes consistentes con Login
                colors = ButtonDefaults.buttonColors(containerColor = MidnightGreen)
            ) {
                if (uiState.loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Regístrate", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Usamos weight para empujar el contenido inferior al final
            Spacer(modifier = Modifier.weight(1f))

            // --- SECCIÓN SOCIAL CON LÍNEA DIVISORIA ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color.LightGray.copy(alpha = 0.2f)
                )

                Text(
                    "ó regístrate con",
                    color = Color.LightGray.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color.LightGray.copy(alpha = 0.2f)
                )
            }

            // Iconos sociales (Asumiendo que R.drawable.* existen)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SocialButton(R.drawable.facebook_icon)
                SocialButton(R.drawable.google_icon)
                SocialButton(R.drawable.instagram_icon)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Enlace "¿Ya tienes una cuenta? Inicia Sesión"
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 50.dp).clickable { onBackClicked() } // Agregamos el clickable aquí
            ) {
                Text(
                    "¿Ya tienes una cuenta? ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
                Text(
                    "Inicia Sesión",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold, // Énfasis
                    fontSize = 16.sp
                )
            }
        }
    }

    // Callback onSuccess when registration is successful
    if (uiState.success) {
        onSuccess(uiState.user!!)
    }
}

// Composable SocialButton (copiado de LoginScreen para consistencia)
@Composable
fun SocialButton(
    @DrawableRes iconId: Int
) {
    Image(
        painter = painterResource(id = iconId),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(45.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}
