package com.hlasoftware.focus.features.forgot_password.data.repository

import kotlinx.coroutines.delay
import java.lang.IllegalArgumentException

class ForgotPasswordRepository {
    suspend fun sendResetPasswordEmail(email: String) {
        // Simulamos una llamada a la red.
        // En una app real, aquí usarías Retrofit u otra librería para hacer una llamada HTTP.
        delay(1000)

        if (email.isEmpty()) {
            throw IllegalArgumentException("El correo electrónico no puede estar vacío.")
        }

        // Aquí podrías agregar lógica para simular un correo no encontrado, etc.
        // Por ahora, si no está vacío, asumimos que el envío fue exitoso.
    }
}