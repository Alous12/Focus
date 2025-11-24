package com.hlasoftware.focus.features.add_task.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.hlasoftware.focus.features.add_task.domain.model.Task
import com.hlasoftware.focus.features.add_task.domain.repository.AddTaskRepository
import kotlinx.coroutines.tasks.await

class AddTaskRepositoryImpl(private val firestore: FirebaseFirestore) : AddTaskRepository {
    override suspend fun addTask(task: Task): Result<Unit> {
        return try {
            firestore.collection("tasks").add(task).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
