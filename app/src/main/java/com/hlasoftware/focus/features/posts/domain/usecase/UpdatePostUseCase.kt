package com.hlasoftware.focus.features.posts.domain.usecase

import android.net.Uri
import com.hlasoftware.focus.features.posts.domain.repository.IPostRepository

class UpdatePostUseCase(private val postRepository: IPostRepository) {
    suspend operator fun invoke(postId: String, text: String, imageUri: Uri?) =
        postRepository.updatePost(postId, text, imageUri)
}
