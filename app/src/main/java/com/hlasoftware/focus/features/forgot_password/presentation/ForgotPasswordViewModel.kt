package com.hlasoftware.focus.features.forgot_password.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.forgot_password.domain.usecase.SendResetPasswordEmailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val useCase: SendResetPasswordEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onSendEmailClicked(email: String) {
        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading

            try {
                useCase(email)
                _uiState.value = ForgotPasswordUiState.Success
            } catch (e: Exception) {
                _uiState.value = ForgotPasswordUiState.Error(e.message ?: "Ocurrió un error desconocido.")
            }
        }
    }
}

// Representa los diferentes estados de la pantalla.
sealed class ForgotPasswordUiState {
    object Idle : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    object Success : ForgotPasswordUiState()
    data class Error(val message: String) : ForgotPasswordUiState()
}