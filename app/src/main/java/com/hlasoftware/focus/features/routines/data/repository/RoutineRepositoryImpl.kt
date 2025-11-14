package com.hlasoftware.focus.features.routines.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.routines.domain.model.Routine
import com.hlasoftware.focus.features.routines.domain.repository.RoutineRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RoutineRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : RoutineRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    override fun getRoutines(): Flow<List<Routine>> = callbackFlow {
        val listener = firestore.collection("users").document(userId).collection("routines")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    // Manually map documents to include the ID
                    val routines = snapshot.documents.mapNotNull {
                        it.toObject(Routine::class.java)?.copy(id = it.id)
                    }
                    trySend(routines).isSuccess
                } else {
                    close(IllegalStateException("Snapshot was null"))
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addRoutine(routine: Routine): Result<Unit> {
        return try {
            // Add the routine and copy the current user's ID into the document
            firestore.collection("users").document(userId).collection("routines")
                .add(routine.copy(userId = userId)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
