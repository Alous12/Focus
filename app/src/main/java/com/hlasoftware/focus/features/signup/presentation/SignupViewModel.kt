package com.hlasoftware.focus.features.signup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.signup.domain.model.SignUpModel
import com.hlasoftware.focus.features.signup.domain.model.UserProfile
import com.hlasoftware.focus.features.signup.domain.usecase.SignUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SignUpUiState(
    val name: String = "",
    val birthdate: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val user: UserProfile? = null // ← perfil resultante al registrarse
)

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(name = name, error = null)
    }

    fun onBirthdateChanged(birthdate: String) {
        _uiState.value = _uiState.value.copy(birthdate = birthdate, error = null)
    }

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, error = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun onConfirmPasswordChanged(confirm: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirm, error = null)
    }

    fun reset() {
        _uiState.value = SignUpUiState()
    }

    fun onSignUpClick() {
        val current = _uiState.value

        // Validaciones locales
        if (
            current.name.isBlank() ||
            current.birthdate.isBlank() ||
            current.email.isBlank() ||
            current.password.isBlank() ||
            current.confirmPassword.isBlank()
        ) {
            _uiState.value = current.copy(error = "Todos los campos son obligatorios")
            return
        }

        if (!current.email.contains("@")) {
            _uiState.value = current.copy(error = "Email no válido")
            return
        }

        if (current.password.length < 6) {
            _uiState.value = current.copy(error = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        if (current.password != current.confirmPassword) {
            _uiState.value = current.copy(error = "Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(loading = true, error = null)

            try {
                // Tu SignUpModel del dominio puede tener (name, birthdate, email, password, confirmPassword)
                // El use case internamente usará email/password y guardará el perfil en Firestore
                val params = SignUpModel(
                    name = current.name,
                    birthdate = current.birthdate,
                    email = current.email,
                    password = current.password,
                    confirmPassword = current.confirmPassword
                )

                // UseCase retorna UserProfile si todo sale bien
                val profile: UserProfile = signUpUseCase(params)

                _uiState.value = _uiState.value.copy(
                    loading = false,
                    success = true,
                    user = profile,
                    error = null
                )
            } catch (e: Exception) {
                // Por ejemplo: email en uso, formato inválido, etc.
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    success = false,
                    user = null,
                    error = e.message ?: "No se pudo registrar. Inténtalo de nuevo."
                )
            }
        }
    }
}
