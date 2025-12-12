package com.hlasoftware.focus.features.signup.presentation

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.hlasoftware.focus.R
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.ui.theme.AuthBackground
import com.hlasoftware.focus.ui.theme.MidnightGreen
import com.hlasoftware.focus.ui.theme.component.BackgroundShapes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onBackClicked: () -> Unit,
    onSuccess: (user: ProfileModel) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.error
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result: ActivityResult ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { token ->
                    viewModel.onGoogleSignIn(token)
                }
            } catch (e: ApiException) {
                viewModel.onGoogleSignInFailed()
            }
        }
    )

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) } // Estado para el DatePicker

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
                text = stringResource(id = R.string.signup_title),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            val lineTextFieldColors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = colorResource(id = R.color.login_indicator_gray),
                cursorColor = Color.White
            )
            val lineTextStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp)
            val lineLabelStyle: @Composable (String) -> (@Composable () -> Unit) = { label ->
                { Text(label, color = colorResource(id = R.color.login_label_gray), fontSize = 16.sp) }
            }

            TextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChanged,
                label = lineLabelStyle(stringResource(id = R.string.signup_name_label)),
                singleLine = true,
                colors = lineTextFieldColors,
                textStyle = lineTextStyle,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = uiState.birthdate,
                onValueChange = {},
                readOnly = true,
                label = lineLabelStyle(stringResource(id = R.string.signup_birthdate_label)),
                singleLine = true,
                colors = lineTextFieldColors,
                textStyle = lineTextStyle,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = stringResource(id = R.string.signup_select_date_button),
                        tint = Color.LightGray,
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = uiState.rawEmail,
                onValueChange = viewModel::onEmailChanged,
                label = lineLabelStyle(stringResource(id = R.string.signup_email_label)),
                singleLine = true,
                colors = lineTextFieldColors,
                textStyle = lineTextStyle,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChanged,
                label = lineLabelStyle(stringResource(id = R.string.signup_password_label)),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = stringResource(id = R.string.signup_toggle_password_visibility),
                            tint = Color.LightGray
                        )
                    }
                },
                colors = lineTextFieldColors,
                textStyle = lineTextStyle,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChanged,
                label = lineLabelStyle(stringResource(id = R.string.signup_confirm_password_label)),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = stringResource(id = R.string.signup_toggle_confirm_password_visibility),
                            tint = Color.LightGray
                        )
                    }
                },
                colors = lineTextFieldColors,
                textStyle = lineTextStyle,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            if (uiState.success) {
                Text(stringResource(id = R.string.signup_success_message), color = Color.Green, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }

            Button(
                onClick = { viewModel.onSignUpClick() },
                enabled = !uiState.loading && uiState.name.isNotBlank() && uiState.rawEmail.isNotBlank() && uiState.password.isNotBlank() && uiState.confirmPassword.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = MidnightGreen)
            ) {
                if (uiState.loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(id = R.string.signup_button), fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
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
                    stringResource(id = R.string.signup_social_prompt),
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
                modifier = Modifier.padding(bottom = 50.dp).clickable { onBackClicked() }
            ) {
                Text(
                    stringResource(id = R.string.signup_has_account),
                    color = colorResource(id = R.color.login_no_account_gray),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    stringResource(id = R.string.signup_login),
                    color = colorResource(id = R.color.title_color),
                    fontSize = 16.sp
                )
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                        viewModel.onBirthdateChanged(formatter.format(Date(it)))
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(id = R.string.datepicker_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(id = R.string.datepicker_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (uiState.success) {
        uiState.user?.let { onSuccess(it) }
    }
}

@Composable
fun SocialButton(@DrawableRes iconId: Int, onClick: () -> Unit) {
    Image(
        painter = painterResource(id = iconId),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(45.dp).clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick)
    )
}
