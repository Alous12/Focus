package com.hlasoftware.focus.features.signup.domain.repository

import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.signup.domain.model.SignUpModel

interface SignUpRepository {
    suspend fun signUp(params: SignUpModel): ProfileModel
    suspend fun signUpWithGoogle(token: String): ProfileModel
}
