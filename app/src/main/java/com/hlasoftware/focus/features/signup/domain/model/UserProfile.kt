package com.hlasoftware.focus.features.signup.domain.model

data class UserProfile (
    val uid: String = "",
    val email: String = "",
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)