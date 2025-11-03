package com.hlasoftware.focus.features.login.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import androidx.annotation.DrawableRes
import com.hlasoftware.focus.R
import com.hlasoftware.focus.features.login.domain.model.UserModel
import com.hlasoftware.focus.ui.theme.AuthBackground
import com.hlasoftware.focus.ui.theme.MidnightGreen
import com.hlasoftware.focus.ui.theme.component.BackgroundShapes
import androidx.compose.ui.text.TextStyle

@Composable
fun LoginScreen(
    vm: LoginViewModel = koinViewModel(),
    onForgotPasswordClicked: () -> Unit,
    onLoginSuccess: (user: UserModel) -> Unit,
    onSignUpClicked: () -> Unit
) {
    val uiState by vm.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.success) {
        if (uiState.success && uiState.user != null) {
            onLoginSuccess(uiState.user!!)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthBackground)
    ) {
        // CAPA 1: Formas de fondo
        BackgroundShapes(modifier = Modifier.fillMaxSize())

        // CAPA 2: Contenido de la UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(130.dp))

            Text(
                text = "Inicia Sesión",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 50.dp)
            )

            // --- CAMPO EMAIL/NÚMERO ---
            TextField(
                value = uiState.email,
                onValueChange = vm::onEmailChanged,
                label = { Text("Ingresa tu número o correo electrónico", color = Color.LightGray.copy(alpha = 0.7f), fontSize = 16.sp) },
                singleLine = true, // CORREGIDO: Evita el salto de línea
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.LightGray.copy(alpha = 0.5f),
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- CAMPO CONTRASEÑA ---
            TextField(
                value = uiState.password,
                onValueChange = vm::onPasswordChanged,
                label = { Text("Ingresa tu contraseña", color = Color.LightGray.copy(alpha = 0.7f), fontSize = 16.sp) },
                singleLine = true, // Añadido por consistencia
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector  = image, contentDescription = "Toggle password visibility", tint = Color.LightGray)
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.LightGray.copy(alpha = 0.5f),
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Texto "¿Olvidaste tu contraseña?" alineado a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onForgotPasswordClicked, contentPadding = PaddingValues(0.dp)) {
                    Text(
                        "¿Olvidaste tu contraseña?",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTÓN INICIA SESIÓN ---
            Button(
                onClick = { vm.login() },
                enabled = !uiState.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = MidnightGreen)
            ) {
                if (uiState.loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(
                        "Inicia Sesión",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            uiState.error?.let { error ->
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- SECCIÓN SOCIAL ---

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
                    "ó inicia sesión con",
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

            // Iconos sociales
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

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 50.dp)
            ) {
                Text(
                    "¿No tienes una cuenta? ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
                TextButton(onClick = onSignUpClicked, contentPadding = PaddingValues(0.dp)) {
                    Text(
                        "Regístrate",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


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