package com.hlasoftware.focus.features.login.domain.usecase

import com.hlasoftware.focus.features.login.domain.model.UserModel
import com.hlasoftware.focus.features.login.domain.repository.ILoginRepository

class GoogleSignInUseCase(private val repository: ILoginRepository) {
    suspend operator fun invoke(token: String): UserModel {
        return repository.signInWithGoogle(token)
    }
}