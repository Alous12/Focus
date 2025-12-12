package com.hlasoftware.focus.features.signup.domain.model

import com.hlasoftware.focus.core.domain.model.NonEmptyString
import com.hlasoftware.focus.features.login.domain.model.Email
import com.hlasoftware.focus.features.login.domain.model.Password

data class SignUpModel(
    val name: NonEmptyString,
    val birthdate: String,
    val email: Email,
    val password: Password,
)
