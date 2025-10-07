package com.hlasoftware.focus.features.signup.domain.model

data class SignUpModel(
    val name: String,
    val birthdate: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)