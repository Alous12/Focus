package com.hlasoftware.focus.features.workgroup_details.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.workgroup_details.domain.model.WorkgroupDetails
import com.hlasoftware.focus.features.workgroup_details.domain.model.WorkgroupMember
import com.hlasoftware.focus.features.workgroup_details.domain.model.WorkgroupTask
import com.hlasoftware.focus.features.workgroup_details.domain.repository.WorkgroupDetailsRepository
import com.hlasoftware.focus.features.workgroups.domain.model.Workgroup
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class WorkgroupDetailsRepositoryImpl(private val firestore: FirebaseFirestore) : WorkgroupDetailsRepository {

    override fun getWorkgroupDetails(workgroupId: String): Flow<WorkgroupDetails> {
        val workgroupFlow = callbackFlow<Workgroup> {
            val listener = firestore.collection("workgroups").document(workgroupId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        close(e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val workgroup = snapshot.toObject(Workgroup::class.java)?.copy(id = snapshot.id)
                        if (workgroup != null) {
                            trySend(workgroup)
                        } else {
                            close(Exception("Workgroup not found"))
                        }
                    } else {
                        close(Exception("Workgroup not found"))
                    }
                }
            awaitClose { listener.remove() }
        }

        // Placeholder flows that emit an empty list. This will be implemented later.
        val membersFlow = flowOf(emptyList<WorkgroupMember>())
        val tasksFlow = flowOf(emptyList<WorkgroupTask>())

        return combine(workgroupFlow, membersFlow, tasksFlow) { workgroup, members, tasks ->
            WorkgroupDetails(workgroup, members, tasks)
        }
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
