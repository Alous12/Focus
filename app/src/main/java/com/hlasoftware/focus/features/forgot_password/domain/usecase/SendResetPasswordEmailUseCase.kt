package com.hlasoftware.focus.features.forgot_password.domain.usecase

import com.hlasoftware.focus.features.forgot_password.data.repository.ForgotPasswordRepository

class SendResetPasswordEmailUseCase(private val repository: ForgotPasswordRepository) {
    suspend operator fun invoke(email: String) {
        // Puedes agregar validaciones adicionales aquí si lo necesitas.
        repository.sendResetPasswordEmail(email)
    }
}