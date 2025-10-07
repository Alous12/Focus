package com.hlasoftware.focus.features.login.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import androidx.annotation.DrawableRes
import com.hlasoftware.focus.R
import com.hlasoftware.focus.features.login.domain.model.UserModel

@Composable
fun LoginScreen(
    vm: LoginViewModel = koinViewModel(),
    onForgotPasswordClicked: () -> Unit,
    onLoginSuccess: (user: UserModel) -> Unit,
    onSignUpClicked: () -> Unit
) {
    val uiState by vm.uiState.collectAsState()


    LaunchedEffect(uiState.success) {
        if (uiState.success && uiState.user != null) {
            onLoginSuccess(uiState.user!!)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E3B46)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "Inicia Sesión",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(50.dp))


            OutlinedTextField(
                value = uiState.email,
                onValueChange = vm::onEmailChanged,
                label = { Text("Ingresa tu número o correo electrónico", color = Color.LightGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = uiState.password,
                onValueChange = vm::onPasswordChanged,
                label = { Text("Ingresa tu contraseña", color = Color.LightGray) },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onForgotPasswordClicked) {
                    Text(
                        "¿Olvidaste tu contraseña?",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))


            Button(
                onClick = { vm.login() },
                enabled = !uiState.loading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
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


            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "ó inicia sesión con",
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SocialButton(R.drawable.facebook_icon)
                    SocialButton(R.drawable.google_icon)
                    SocialButton(R.drawable.instagram_icon)
                }

                Spacer(modifier = Modifier.height(24.dp))


                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "¿No tienes una cuenta? ",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Button(onClick = onSignUpClicked) {
                        Text("Regístrate")
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))
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
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(10.dp))
    )
}
