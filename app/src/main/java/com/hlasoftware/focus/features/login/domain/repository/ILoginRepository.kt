package com.hlasoftware.focus.features.login.domain.repository

import com.hlasoftware.focus.features.login.domain.model.UserModel

interface ILoginRepository {
    suspend fun login(email: String, password: String): UserModel
    suspend fun signInWithGoogle(token: String): UserModel
    fun currentUser(): UserModel?
    fun logout()
}