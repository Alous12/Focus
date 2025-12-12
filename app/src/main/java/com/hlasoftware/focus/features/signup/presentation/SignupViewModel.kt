package com.hlasoftware.focus.features.signup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.login.domain.model.Email
import com.hlasoftware.focus.features.login.domain.model.Password
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.signup.domain.model.SignUpModel
import com.hlasoftware.focus.features.signup.domain.usecase.GoogleSignUpUseCase
import com.hlasoftware.focus.features.signup.domain.usecase.SignUpUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SignUpUiState(
    val name: String = "",
    val birthdate: String = "",
    val rawEmail: String = "",
    val email: Email? = null,
    val rawPassword: String = "",
    val password: Password? = null,
    val rawConfirmPassword: String = "",
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val user: ProfileModel? = null,
)

class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val googleSignUpUseCase: GoogleSignUpUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    private var errorJob: Job? = null

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(name = name, error = null)
    }

    fun onBirthdateChanged(birthdate: String) {
        _uiState.value = _uiState.value.copy(birthdate = birthdate, error = null)
    }

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

    fun onConfirmPasswordChanged(confirm: String) {
        _uiState.value = _uiState.value.copy(rawConfirmPassword = confirm, error = null)
    }

    fun onGoogleSignIn(token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                val user = googleSignUpUseCase(token)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    user = user,
                    success = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Error al registrarse con Google",
                    success = false
                )
            }
        }
    }

    fun onGoogleSignInFailed() {
        _uiState.value = _uiState.value.copy(error = "Error al registrarse con Google")
    }

    fun reset() {
        _uiState.value = SignUpUiState()
    }

    fun onSignUpClick() {
        val current = _uiState.value

        if (current.name.isBlank() || current.birthdate.isBlank()) {
            _uiState.value = current.copy(error = "Todos los campos son obligatorios")
            return
        }

        if (current.email == null) {
            _uiState.value = current.copy(error = current.error ?: "Email con formato invalido")
            return
        }

        if (current.password == null) {
            _uiState.value = current.copy(error = current.error ?: "Contraseña con formato invalido")
            return
        }

        if (current.password.value != current.rawConfirmPassword) {
            _uiState.value = current.copy(error = "Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(loading = true, error = null)

            try {
                val params = SignUpModel(
                    name = current.name,
                    birthdate = current.birthdate,
                    email = current.email.value,
                    password = current.password.value,
                    confirmPassword = current.rawConfirmPassword
                )

                val profile: ProfileModel = signUpUseCase(params)

                _uiState.value = _uiState.value.copy(
                    loading = false,
                    success = true,
                    user = profile,
                    error = null
                )
            } catch (e: Exception) {
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