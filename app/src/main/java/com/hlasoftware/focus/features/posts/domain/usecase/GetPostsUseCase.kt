package com.hlasoftware.focus.features.posts.domain.usecase

import com.hlasoftware.focus.features.posts.domain.repository.IPostRepository

class GetPostsUseCase(private val postRepository: IPostRepository) {
    operator fun invoke(userId: String) = postRepository.getPosts(userId)
}
