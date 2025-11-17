package com.hlasoftware.focus.features.workgroups.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.workgroups.domain.model.Workgroup
import com.hlasoftware.focus.features.workgroups.domain.repository.WorkgroupRepository
import kotlinx.coroutines.tasks.await

class WorkgroupRepositoryImpl(private val firestore: FirebaseFirestore) : WorkgroupRepository {
    override suspend fun getWorkgroups(userId: String): List<Workgroup> {
        // This is a placeholder implementation. You'll need to adjust it to your Firestore structure.
        return try {
            val snapshot = firestore.collection("workgroups")
                .whereArrayContains("members", userId)
                .get()
                .await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(Workgroup::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
