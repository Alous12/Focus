package com.hlasoftware.focus.features.login.domain.usecase

import com.hlasoftware.focus.features.login.data.repository.LoginRepository
import com.hlasoftware.focus.features.login.domain.model.UserModel

class LoginUseCase(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(email: String, password: String): UserModel {
        require(email.isNotBlank()) { "El email es obligatorio" }
        require(password.isNotBlank()) { "La contraseña es obligatoria" }

        return repository.login(email, password)
    }
}