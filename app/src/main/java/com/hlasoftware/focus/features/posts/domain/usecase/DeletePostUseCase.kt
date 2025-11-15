package com.hlasoftware.focus.features.posts.domain.usecase

import com.hlasoftware.focus.features.posts.domain.repository.IPostRepository

class DeletePostUseCase(private val postRepository: IPostRepository) {
    suspend operator fun invoke(postId: String) = postRepository.deletePost(postId)
}
