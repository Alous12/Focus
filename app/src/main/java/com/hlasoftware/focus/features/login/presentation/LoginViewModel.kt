package com.hlasoftware.focus.features.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.login.domain.model.UserModel
import com.hlasoftware.focus.features.login.domain.usecase.LoginUseCase
import com.hlasoftware.focus.features.login.domain.usecase.GoogleSignInUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val user: UserModel? = null,
    val success: Boolean = false
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private var errorJob: Job? = null

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email.filter { it != '\n' }, error = null)
    }

    fun onPasswordChanged(password: String) {
        errorJob?.cancel() // Cancela el trabajo anterior si existe

        if (password.length <= 20) {
            _uiState.value = _uiState.value.copy(password = password, error = null)
        } else {
            _uiState.value = _uiState.value.copy(password = password, error = "La contraseña no puede exceder los 20 caracteres.")
            errorJob = viewModelScope.launch {
                delay(2000) // Espera 2 segundos
                if (_uiState.value.error == "La contraseña no puede exceder los 20 caracteres.") {
                    _uiState.value = _uiState.value.copy(error = null)
                }
            }
        }
    }

    fun login() {
        val current = _uiState.value

        if (current.email.isBlank() || current.password.isBlank()) {
            _uiState.value = current.copy(error = "Completa email y contraseña")
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(loading = true, error = null)
            try {
                val user = loginUseCase(current.email, current.password)
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