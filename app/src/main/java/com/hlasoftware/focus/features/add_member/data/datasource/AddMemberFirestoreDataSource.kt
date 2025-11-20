package com.hlasoftware.focus.features.add_member.data.datasource

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.add_member.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AddMemberFirestoreDataSource(private val firestore: FirebaseFirestore) : IAddMemberDataSource {

    override fun searchUsersByEmail(email: String): Flow<List<User>> = callbackFlow {
        if (email.isEmpty()) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val query = firestore.collection("users")
            .whereGreaterThanOrEqualTo("email", email)
            .whereLessThanOrEqualTo("email", email + '\uf8ff')

        val listener = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            val users = snapshot?.documents?.mapNotNull {
                it.toUser()
            } ?: emptyList()

            trySend(users)
        }

        awaitClose { listener.remove() }
    }

    override suspend fun addMemberToWorkgroup(workgroupId: String, userId: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        firestore.collection("workgroups").document(workgroupId)
            .update("members", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener {
                continuation.resume(Result.failure(it))
            }
    }
}

private fun DocumentSnapshot.toUser(): User? {
    return try {
        val name = getString("name") ?: ""
        val email = getString("email") ?: ""
        val profilePictureUrl = getString("profilePictureUrl")
        User(id, name, email, profilePictureUrl)
    } catch (e: Exception) {
        null
    }
}