package com.hlasoftware.focus.features.profile.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.profile.domain.repository.IProfileRepository
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth // Mantenido por consistencia con DI, aunque no se use directamente
): IProfileRepository {

    override suspend fun fetchData(userId: String): Result<ProfileModel> {
        // Manejar el caso de un ID de usuario vacío
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("User ID cannot be blank."))
        }

        return try {
            // Usar el userId pasado como parámetro para la consulta
            val documentReference = firestore.collection("users").document(userId)
            val documentSnapshot = documentReference.get().await()

            if (documentSnapshot.exists()) {
                val profile = documentSnapshot.toObject(ProfileModel::class.java)
                if (profile != null) {
                    Result.success(profile)
                } else {
                    Result.failure(Exception("Error mapping Firebase data to ProfileModel."))
                }
            } else {
                Result.failure(NoSuchElementException("Profile data not found for user: $userId"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}