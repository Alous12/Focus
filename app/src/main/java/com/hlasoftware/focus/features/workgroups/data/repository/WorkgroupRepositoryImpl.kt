package com.hlasoftware.focus.features.workgroups.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.workgroups.domain.model.Workgroup
import com.hlasoftware.focus.features.workgroups.domain.repository.WorkgroupRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class WorkgroupRepositoryImpl(private val firestore: FirebaseFirestore) : WorkgroupRepository {

    override fun getWorkgroups(userId: String): Flow<List<Workgroup>> = callbackFlow {
        val listener = firestore.collection("workgroups")
            .whereArrayContains("members", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val workgroups = snapshot.documents.mapNotNull { document ->
                        document.toObject(Workgroup::class.java)?.copy(id = document.id)
                    }
                    trySend(workgroups)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun deleteWorkgroup(workgroupId: String): Result<Unit> {
        return try {
            firestore.collection("workgroups").document(workgroupId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
