package com.hlasoftware.focus.features.signup.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.signup.domain.model.SignUpModel
import com.hlasoftware.focus.features.signup.domain.repository.SignUpRepository
import kotlinx.coroutines.tasks.await

class SignUpRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : SignUpRepository {

    override suspend fun signUp(params: SignUpModel): ProfileModel {
        val authResult = auth
            .createUserWithEmailAndPassword(params.email.value, params.password.value)
            .await()

        val uid = authResult.user?.uid
            ?: throw IllegalStateException("No se obtuvo UID de Firebase Auth")

        val profile = ProfileModel(
            uid = uid,
            email = params.email.value,
            name = params.name.value, // Corregido: usar .value
            birthdate = params.birthdate,
            createdAt = System.currentTimeMillis(),
            pathUrl = "",
            summary = "",
        )

        db.collection("users").document(uid).set(profile).await()

        authResult.user?.updateProfile(
            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(profile.name)
                .build()
        )?.await()

        return profile
    }

    override suspend fun signUpWithGoogle(token: String): ProfileModel {
        val credential = GoogleAuthProvider.getCredential(token, null)
        val authResult = auth.signInWithCredential(credential).await()
        val user = authResult.user ?: throw IllegalStateException("No se pudo obtener el usuario de Firebase")

        val userDoc = db.collection("users").document(user.uid).get().await()

        if (userDoc.exists()) {
            return userDoc.toObject(ProfileModel::class.java)
                ?: throw IllegalStateException("No se pudo convertir el documento a ProfileModel")
        } else {
            val profile = ProfileModel(
                uid = user.uid,
                email = user.email ?: "",
                name = user.displayName ?: "",
                pathUrl = user.photoUrl?.toString() ?: "",
                createdAt = System.currentTimeMillis(),
                summary = ""
            )
            db.collection("users").document(user.uid).set(profile).await()
            return profile
        }
    }
}
