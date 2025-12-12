package com.hlasoftware.focus.features.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.login.domain.model.Email
import com.hlasoftware.focus.features.login.domain.model.Password
import com.hlasoftware.focus.features.login.domain.model.UserModel
import com.hlasoftware.focus.features.login.domain.usecase.GoogleSignInUseCase
import com.hlasoftware.focus.features.login.domain.usecase.LoginUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val rawEmail: String = "",
    val email: Email? = null,
    val rawPassword: String = "",
    val password: Password? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val user: UserModel? = null,
    val success: Boolean = false,
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private var errorJob: Job? = null

    fun onEmailChanged(email: String) {
        val emailSanitized = email.filter { it != '\n' }
        try {
            _uiState.value = _uiState.value.copy(
                rawEmail = emailSanitized,
                email = Email.create(emailSanitized),
                error = null
            )
        } catch (e: IllegalArgumentException) {
            _uiState.value = _uiState.value.copy(rawEmail = emailSanitized, email = null, error = e.message)
        }
    }

    fun onPasswordChanged(password: String) {
        try {
            _uiState.value = _uiState.value.copy(
                rawPassword = password,
                password = Password.create(password),
                error = null
            )
        } catch (e: IllegalArgumentException) {
            _uiState.value = _uiState.value.copy(rawPassword = password, password = null, error = e.message)
        }
    }

    fun login() {
        val current = _uiState.value
        if (current.email == null) {
            _uiState.value = current.copy(error = current.error ?: "Email con formato invalido")
            return
        }

        if (current.password == null) {
            _uiState.value = current.copy(error = current.error ?: "Contraseña con formato invalido")
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(loading = true, error = null)
            try {
                val user = loginUseCase(current.email.value, current.password.value)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    user = user,
                    success = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Error al iniciar sesión",
                    success = false
                )
            }
        }
    }

    fun onGoogleSignIn(token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                val user = googleSignInUseCase(token)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    user = user,
                    success = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Error al iniciar sesión con Google",
                    success = false
                )
            }
        }
    }

    fun onGoogleSignInFailed() {
        _uiState.value = _uiState.value.copy(error = "Error al iniciar sesión con Google")
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
