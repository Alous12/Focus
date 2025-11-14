package com.hlasoftware.focus.features.posts.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.hlasoftware.focus.features.posts.domain.model.PostModel
import com.hlasoftware.focus.features.posts.domain.repository.IPostRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

class PostRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : IPostRepository {

    override fun getPosts(userId: String): Flow<List<PostModel>> = callbackFlow {
        val listener = firestore.collection("posts")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val posts = snapshot?.toObjects(PostModel::class.java) ?: emptyList()
                trySend(posts)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createPost(userId: String, text: String, imageUri: Uri?): Result<Unit> = runCatching {
        val imageUrl = imageUri?.let { uploadImage(it) }
        val post = PostModel(
            userId = userId,
            text = text,
            imageUrl = imageUrl,
            createdAt = Date(),
            updatedAt = Date()
        )
        firestore.collection("posts").add(post).await()
    }

    override suspend fun updatePost(postId: String, text: String, imageUri: Uri?): Result<Unit> = runCatching {
        val imageUrl = imageUri?.let { uploadImage(it) }
        val updates = mutableMapOf<String, Any>(
            "text" to text,
            "updatedAt" to Date()
        )
        imageUrl?.let { updates["imageUrl"] = it }
        firestore.collection("posts").document(postId).update(updates).await()
    }

    override suspend fun deletePost(postId: String): Result<Unit> = runCatching {
        firestore.collection("posts").document(postId).delete().await()
    }

    private suspend fun uploadImage(uri: Uri): String {
        val storageRef = storage.reference.child("post_images/${UUID.randomUUID()}")
        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }
}
