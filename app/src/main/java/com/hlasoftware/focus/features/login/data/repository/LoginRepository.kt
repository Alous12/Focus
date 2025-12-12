package com.hlasoftware.focus.features.login.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hlasoftware.focus.features.login.domain.model.Email
import com.hlasoftware.focus.features.login.domain.model.UserModel
import com.hlasoftware.focus.features.login.domain.repository.ILoginRepository
import kotlinx.coroutines.tasks.await

class LoginRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ILoginRepository {

    override suspend fun login(email: String, password: String): UserModel {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        val firebaseUser = result.user ?: throw IllegalStateException("Usuario no encontrado")

        return UserModel(
            email = Email.create(firebaseUser.email ?: ""),
            userId = firebaseUser.uid
        )
    }

    override suspend fun signInWithGoogle(token: String): UserModel {
        val credential = GoogleAuthProvider.getCredential(token, null)
        val result = auth.signInWithCredential(credential).await()
        val firebaseUser = result.user ?: throw IllegalStateException("Usuario no encontrado")

        return UserModel(
            email = Email.create(firebaseUser.email ?: ""),
            userId = firebaseUser.uid
        )
    }

    override fun currentUser(): UserModel? {
        val user = auth.currentUser
        return user?.let {
            UserModel(
                email = Email.create(it.email ?: ""),
                userId = it.uid
            )
        }
    }

    override fun logout() {
        auth.signOut()
    }
}