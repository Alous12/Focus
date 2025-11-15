package com.hlasoftware.focus.features.posts.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PostModel(
    val id: String = "",
    val userId: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null,
)
