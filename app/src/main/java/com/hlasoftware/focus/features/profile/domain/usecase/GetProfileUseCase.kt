package com.hlasoftware.focus.features.profile.domain.usecase

import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.profile.domain.repository.IProfileRepository

class GetProfileUseCase(
    private val repository: IProfileRepository
) {
    suspend operator fun invoke(userId: String): Result<ProfileModel> {
        // El repositorio ahora necesita el userId para buscar el perfil
        return repository.fetchData(userId)
    }
}
