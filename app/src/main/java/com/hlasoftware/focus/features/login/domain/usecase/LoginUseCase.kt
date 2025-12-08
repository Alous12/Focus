package com.hlasoftware.focus.features.login.domain.usecase

import com.hlasoftware.focus.features.login.domain.model.UserModel
import com.hlasoftware.focus.features.login.domain.repository.ILoginRepository

class LoginUseCase(
    private val repository: ILoginRepository
) {
    suspend operator fun invoke(email: String, password: String): UserModel {
        require(email.isNotBlank()) { "El email es obligatorio" }
        require(password.isNotBlank()) { "La contraseña es obligatoria" }

        return repository.login(email, password)
    }
}
