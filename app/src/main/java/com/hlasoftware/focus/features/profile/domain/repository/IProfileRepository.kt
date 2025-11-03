package com.hlasoftware.focus.features.profile.domain.repository

import com.hlasoftware.focus.features.profile.domain.model.ProfileModel

interface IProfileRepository {
    // El método ahora requiere el userId para buscar el perfil correcto
    suspend fun fetchData(userId: String): Result<ProfileModel>
}
