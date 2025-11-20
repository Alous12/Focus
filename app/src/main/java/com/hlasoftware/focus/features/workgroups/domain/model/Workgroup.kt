package com.hlasoftware.focus.features.workgroups.domain.model

data class Workgroup(
    val id: String = "",
    val name: String = "",
    val imageUrl: String? = null,
    val admin: String = "",
    val adminName: String = "",
    val description: String? = null,
    val code: String = "",
    val members: List<String> = emptyList()
)
