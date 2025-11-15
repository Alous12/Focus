package com.hlasoftware.focus.features.profile.domain.usecase

import com.hlasoftware.focus.features.profile.domain.repository.IProfileRepository

class DeleteAccountUseCase(private val repository: IProfileRepository) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return repository.deleteAccount(userId)
    }
}
