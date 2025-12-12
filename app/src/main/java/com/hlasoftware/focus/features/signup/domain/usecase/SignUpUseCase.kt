package com.hlasoftware.focus.features.signup.domain.usecase

import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.signup.domain.model.SignUpModel
import com.hlasoftware.focus.features.signup.domain.repository.SignUpRepository

class SignUpUseCase(
    private val repo: SignUpRepository
) {
    suspend operator fun invoke(params: SignUpModel): ProfileModel {
        return repo.signUp(params)
    }
}
