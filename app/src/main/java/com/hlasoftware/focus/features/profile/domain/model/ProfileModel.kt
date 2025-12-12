package com.hlasoftware.focus.features.profile.domain.model

import com.google.firebase.firestore.Exclude
import com.hlasoftware.focus.core.domain.model.NonEmptyString
import com.hlasoftware.focus.features.login.domain.model.Email

data class ProfileModel(
    val uid: String = "",
    val email: String = "", // Almacenado en Firestore como String
    val name: String = "", // Almacenado en Firestore como String
    val birthdate: String = "", // Añadido para la edad

    // Campo 'createdAt' que existe en el documento de Firestore
    val createdAt: Long = 0,

    // Campos esenciales para la UI, inicializados a vacío
    val pathUrl: String = "",
    val summary: String = "",
    val language: String = "" // Añadido para guardar el idioma del usuario
) {
    @get:Exclude
    val domainEmail: Email
        get() = Email.create(email)

    @get:Exclude
    val domainName: NonEmptyString
        get() = NonEmptyString.create(name)
}
