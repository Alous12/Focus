package com.hlasoftware.focus.features.profile.domain.model

data class ProfileModel(
    // Todos los campos deben tener valor por defecto para el mapeo de Firebase
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val birthdate: String = "", // Añadido para la edad

    // Campo 'createdAt' que existe en el documento de Firestore
    val createdAt: Long = 0,

    // Campos esenciales para la UI, inicializados a vacío
    val pathUrl: String = "",
    val summary: String = ""
)