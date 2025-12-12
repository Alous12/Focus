package com.hlasoftware.focus.features.login.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.koin.androidx.compose.koinViewModel
import androidx.annotation.DrawableRes
import com.hlasoftware.focus.R
import com.hlasoftware.focus.features.login.domain.model.UserModel
import com.hlasoftware.focus.ui.theme.AuthBackground
import com.hlasoftware.focus.ui.theme.MidnightGreen
import com.hlasoftware.focus.ui.theme.component.BackgroundShapes

@Composable
fun LoginScreen(
    vm: LoginViewModel = koinViewModel(),
    onForgotPasswordClicked: () -> Unit,
    onLoginSuccess: (user: UserModel) -> Unit,
    onSignUpClicked: () -> Unit
) {
    val uiState by vm.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { token ->
                    vm.onGoogleSignIn(token)
                }
            } catch (e: ApiException) {
                vm.onGoogleSignInFailed()
            }
        }
    )


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
        BackgroundShapes(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(130.dp))

            Text(
                text = stringResource(id = R.string.login_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 50.dp)
            )

            TextField(
                value = uiState.rawEmail,
                onValueChange = vm::onEmailChanged,
                label = { Text(stringResource(id = R.string.login_email_label), color = colorResource(id = R.color.login_label_gray), fontSize = 16.sp) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = colorResource(id = R.color.login_indicator_gray),
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = uiState.rawPassword,
                onValueChange = vm::onPasswordChanged,
                label = { Text(stringResource(id = R.string.login_password_label), color = colorResource(id = R.color.login_label_gray), fontSize = 16.sp) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector  = image, contentDescription = stringResource(id = R.string.login_toggle_password_visibility), tint = colorResource(id = R.color.login_forgot_password_gray))
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = colorResource(id = R.color.login_indicator_gray),
                    cursorColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onForgotPasswordClicked, contentPadding = PaddingValues(0.dp)) {
                    Text(
                        stringResource(id = R.string.login_forgot_password),
                        color = colorResource(id = R.color.login_forgot_password_gray),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                        stringResource(id = R.string.login_button),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            uiState.error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = colorResource(id = R.color.login_divider_gray)
                )

                Text(
                    stringResource(id = R.string.login_social_prompt),
                    color = colorResource(id = R.color.login_indicator_gray),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = colorResource(id = R.color.login_divider_gray)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SocialButton(R.drawable.facebook_icon) {}
                SocialButton(R.drawable.google_icon) {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                }
                SocialButton(R.drawable.instagram_icon) {}

            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 50.dp)
            ) {
                Text(
                    stringResource(id = R.string.login_no_account),
                    color = colorResource(id = R.color.login_no_account_gray),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = onSignUpClicked, contentPadding = PaddingValues(0.dp)) {
                    Text(
                        stringResource(id = R.string.login_register),
                        color = colorResource(id = R.color.title_color),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


@Composable
fun SocialButton(
    @DrawableRes iconId: Int,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(id = iconId),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(45.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    )
}