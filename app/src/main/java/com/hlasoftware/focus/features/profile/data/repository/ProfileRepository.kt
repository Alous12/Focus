package com.hlasoftware.focus.features.profile.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.profile.domain.repository.IProfileRepository
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth, 
    private val storage: FirebaseStorage, 
): IProfileRepository {

    override suspend fun fetchData(userId: String): Result<ProfileModel> {
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("User ID cannot be blank."))
        }

        return try {
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

    override suspend fun findUserByEmail(email: String): Result<ProfileModel?> {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                Result.success(null) // No user found
            } else {
                val document = querySnapshot.documents.first()
                val profile = document.toObject(ProfileModel::class.java)
                Result.success(profile)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSummary(userId: String, summary: String): Result<Unit> {
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("User ID cannot be blank."))
        }
        return try {
            firestore.collection("users").document(userId).update("summary", summary).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfilePicture(userId: String, imageUri: Uri): Result<Unit> {
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("User ID cannot be blank."))
        }
        return try {
            val photoUrl = uploadProfileImage(userId, imageUri)
            firestore.collection("users").document(userId).update("pathUrl", photoUrl).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(userId: String): Result<Unit> {
        if (userId.isBlank()) {
            return Result.failure(IllegalArgumentException("User ID cannot be blank."))
        }
        return try {
            firestore.collection("users").document(userId).delete().await()
            auth.currentUser?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadProfileImage(userId: String, uri: Uri): String {
        val storageRef = storage.reference.child("profile_pictures/$userId")
        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }
}