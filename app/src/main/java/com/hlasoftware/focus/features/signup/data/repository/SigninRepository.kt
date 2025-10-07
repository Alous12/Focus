package com.hlasoftware.focus.features.signup.data.repository


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.signup.domain.model.SignUpModel
import com.hlasoftware.focus.features.signup.domain.model.UserProfile
import com.hlasoftware.focus.features.signup.domain.repository.SignUpRepository
import kotlinx.coroutines.tasks.await

class SignUpRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : SignUpRepository {

    private val usersCol = db.collection("users")

    override suspend fun register(params: SignUpModel): UserProfile {
        val authResult = auth
            .createUserWithEmailAndPassword(params.email.trim(), params.password)
            .await()

        val uid = authResult.user?.uid
            ?: throw IllegalStateException("No se obtuvo UID de Firebase Auth")

        val profile = UserProfile(
            uid = uid,
            email = params.email.trim(),
            name = params.name.trim()
        )

        usersCol.document(uid).set(profile).await()

        authResult.user?.updateProfile(
            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(profile.name)
                .build()
        )?.await()

        return profile
    }
}