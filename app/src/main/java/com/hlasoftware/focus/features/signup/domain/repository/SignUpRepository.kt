package com.hlasoftware.focus.features.signup.domain.repository

import com.hlasoftware.focus.features.signup.domain.model.SignUpModel
import com.hlasoftware.focus.features.signup.domain.model.UserProfile

interface SignUpRepository {
    suspend fun register(params: SignUpModel): UserProfile
}