package com.hlasoftware.focus.features.posts.domain.usecase

import android.net.Uri
import com.hlasoftware.focus.features.posts.domain.repository.IPostRepository

class CreatePostUseCase(private val postRepository: IPostRepository) {
    suspend operator fun invoke(userId: String, text: String, imageUri: Uri?) =
        postRepository.createPost(userId, text, imageUri)
}
