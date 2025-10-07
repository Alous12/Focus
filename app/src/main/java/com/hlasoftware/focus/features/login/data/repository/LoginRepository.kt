package com.hlasoftware.focus.features.login.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.hlasoftware.focus.features.login.domain.model.UserModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class LoginRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    suspend fun login(email: String, password: String): UserModel {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        val firebaseUser = result.user ?: throw IllegalStateException("Usuario no encontrado")

        return UserModel(
            userId = firebaseUser.uid,
            email = firebaseUser.email ?: ""

        )
    }

    fun currentUser(): UserModel? {
        val user = auth.currentUser
        return user?.let { UserModel(it.uid, it.email ?: "") }
    }

    fun logout() {
        auth.signOut()
    }
}