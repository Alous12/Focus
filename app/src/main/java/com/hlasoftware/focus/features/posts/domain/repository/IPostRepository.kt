package com.hlasoftware.focus.features.posts.domain.repository

import android.net.Uri
import com.hlasoftware.focus.features.posts.domain.model.PostModel
import kotlinx.coroutines.flow.Flow

interface IPostRepository {
    fun getPosts(userId: String): Flow<List<PostModel>>
    suspend fun createPost(userId: String, text: String, imageUri: Uri?): Result<Unit>
    suspend fun updatePost(postId: String, text: String, imageUri: Uri?): Result<Unit>
    suspend fun deletePost(postId: String): Result<Unit>
}
