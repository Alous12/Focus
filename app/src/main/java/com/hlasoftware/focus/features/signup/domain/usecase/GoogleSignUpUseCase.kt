package com.hlasoftware.focus.features.signup.domain.usecase

import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.signup.domain.repository.SignUpRepository

class GoogleSignUpUseCase(private val repository: SignUpRepository) {
    suspend operator fun invoke(token: String): ProfileModel {
        return repository.signUpWithGoogle(token)
    }
}
