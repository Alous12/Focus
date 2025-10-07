package com.hlasoftware.focus.features.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.login.domain.model.UserModel
import com.hlasoftware.focus.features.login.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
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

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}