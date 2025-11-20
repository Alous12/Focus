package com.hlasoftware.focus.features.join_workgroup.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.join_workgroup.domain.repository.JoinWorkgroupRepository
import kotlinx.coroutines.tasks.await

class JoinWorkgroupRepositoryImpl(private val firestore: FirebaseFirestore) : JoinWorkgroupRepository {
    override suspend fun joinWorkgroup(userId: String, workgroupCode: String): Result<Unit> {
        return try {
            // 1. Find the workgroup by its code
            val workgroupQuery = firestore.collection("workgroups")
                .whereEqualTo("code", workgroupCode)
                .limit(1)
                .get()
                .await()

            if (workgroupQuery.isEmpty) {
                return Result.failure(Exception("Workgroup not found"))
            }

            // 2. Add the user to the workgroup's members
            val workgroupDoc = workgroupQuery.documents.first()
            firestore.collection("workgroups").document(workgroupDoc.id)
                .update("members", FieldValue.arrayUnion(userId))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
