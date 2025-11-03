package com.hlasoftware.focus.features.signup.data.repository


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.signup.domain.model.SignUpModel
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.signup.domain.repository.SignUpRepository
import kotlinx.coroutines.tasks.await

class SignUpRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : SignUpRepository {

    private val usersCol = db.collection("users")

    // 1. CORREGIDO: El tipo de retorno debe ser ProfileModel, no SignUpModel.
    // NOTA: Esto requerirá actualizar la interfaz y el UseCase.
    override suspend fun register(params: SignUpModel): ProfileModel {
        val authResult = auth
            .createUserWithEmailAndPassword(params.email.trim(), params.password)
            .await()

        val uid = authResult.user?.uid
            ?: throw IllegalStateException("No se obtuvo UID de Firebase Auth")

        val profile = ProfileModel(
            uid = uid,
            email = params.email.trim(),
            name = params.name.trim(),
            // 2. CORREGIDO: Se eliminó la coma extra
            createdAt = System.currentTimeMillis(),
            pathUrl = "",
            summary = "",
        )

        // GUARDAR PERFIL COMPLETO EN FIRESTORE
        usersCol.document(uid).set(profile).await()

        // ACTUALIZAR PERFIL DE AUTH
        authResult.user?.updateProfile(
            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(profile.name)
                .build()
        )?.await()

        return profile
    }
}
