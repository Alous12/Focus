package com.hlasoftware.focus.features.signup.domain.usecase

import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.signup.domain.model.SignUpModel
import com.hlasoftware.focus.features.signup.domain.repository.SignUpRepository

class SignUpUseCase(
    private val repo: SignUpRepository
) {
    suspend operator fun invoke(params: SignUpModel): ProfileModel {
        require(params.email.isNotBlank()) { "El correo es requerido." }
        require(params.password.length >= 6) { "La contraseña debe tener al menos 6 caracteres." }

        return repo.register(params)
    }
}