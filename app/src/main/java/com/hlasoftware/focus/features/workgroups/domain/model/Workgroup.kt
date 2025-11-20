package com.hlasoftware.focus.features.workgroups.domain.model

data class Workgroup(
    val id: String = "",
    val name: String = "",
    val imageUrl: String? = null, // La URL de la imagen puede ser nula
    val adminName: String = "",
    val description: String? = null,
    val code: String = ""
)
