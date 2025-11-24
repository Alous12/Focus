package com.hlasoftware.focus.features.workgroup_details.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.add_task.domain.model.Task
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.workgroup_details.domain.model.WorkgroupDetails
import com.hlasoftware.focus.features.workgroup_details.domain.model.WorkgroupMember
import com.hlasoftware.focus.features.workgroup_details.domain.model.WorkgroupTask
import com.hlasoftware.focus.features.workgroup_details.domain.repository.WorkgroupDetailsRepository
import com.hlasoftware.focus.features.workgroups.domain.model.Workgroup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class WorkgroupDetailsRepositoryImpl(private val firestore: FirebaseFirestore) : WorkgroupDetailsRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
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
                            close(Exception("Workgroup with ID $workgroupId not found"))
                        }
                    } else {
                        close(Exception("Workgroup not found"))
                    }
                }
            awaitClose { listener.remove() }
        }

        val membersFlow = workgroupFlow.flatMapLatest { workgroup ->
            if (workgroup.members.isEmpty()) {
                flowOf(emptyList<WorkgroupMember>())
            } else {
                callbackFlow {
                    val listener = firestore.collection("users").whereIn(FieldPath.documentId(), workgroup.members)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                close(e)
                                return@addSnapshotListener
                            }
                            if (snapshot != null) {
                                val members = snapshot.documents.mapNotNull { doc ->
                                    val profile = doc.toObject(ProfileModel::class.java)
                                    profile?.let {
                                        WorkgroupMember(
                                            id = doc.id,
                                            name = it.name,
                                            color = "#%06X".format(0xFFFFFF and it.name.hashCode())
                                        )
                                    }
                                }
                                trySend(members)
                            }
                        }
                    awaitClose { listener.remove() }
                }
            }
        }

        val tasksFlow = callbackFlow<List<Task>> {
            val listener = firestore.collection("tasks")
                .whereEqualTo("workgroupId", workgroupId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        close(e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val tasks = snapshot.documents.mapNotNull { document ->
                            document.toObject(Task::class.java)?.copy(id = document.id)
                        }
                        trySend(tasks)
                    }
                }
            awaitClose { listener.remove() }
        }.flatMapLatest { tasks ->
            val memberIds = tasks.flatMap { it.assignedMemberIds }.distinct()
            if (memberIds.isEmpty()) {
                flowOf(tasks.map { WorkgroupTask(it.id, it.name, emptyList()) })
            } else {
                flowOf(firestore.collection("users").whereIn(FieldPath.documentId(), memberIds).get().await()).map { userSnapshot ->
                    val users = userSnapshot.documents.mapNotNull { it.toObject(ProfileModel::class.java)?.copy(uid = it.id) }
                    tasks.map { task ->
                        val implicatedMembers = users.filter { user -> task.assignedMemberIds.contains(user.uid) }.map {
                            WorkgroupMember(it.uid, it.name, "#%06X".format(0xFFFFFF and it.name.hashCode()))
                        }
                        WorkgroupTask(task.id, task.name, implicatedMembers)
                    }
                }
            }
        }

        return combine(workgroupFlow, membersFlow, tasksFlow) { workgroup, members, tasks ->
            WorkgroupDetails(workgroup, members, tasks)
        }
    }
}
