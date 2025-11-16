package com.hlasoftware.focus.features.create_workgroup.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hlasoftware.focus.features.create_workgroup.domain.repository.CreateWorkgroupRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CreateWorkgroupRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : CreateWorkgroupRepository {

    override suspend fun createWorkgroup(
        name: String,
        description: String,
        imageUri: Uri?,
        adminId: String,
        adminName: String
    ): Result<Unit> {
        return try {
            var imageUrl: String? = null
            if (imageUri != null) {
                val imageRef = storage.reference.child("workgroup_images/${UUID.randomUUID()}")
                imageRef.putFile(imageUri).await()
                imageUrl = imageRef.downloadUrl.await().toString()
            }

            val workgroupData = hashMapOf(
                "name" to name,
                "description" to description,
                "imageUrl" to imageUrl,
                "admin" to adminId,
                "adminName" to adminName, // Guardar el nombre del admin
                "members" to listOf(adminId),
                "code" to UUID.randomUUID().toString().substring(0, 6).uppercase()
            )

            firestore.collection("workgroups").add(workgroupData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
