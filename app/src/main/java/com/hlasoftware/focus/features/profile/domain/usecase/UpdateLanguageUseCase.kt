package com.hlasoftware.focus.features.profile.domain.usecase

import com.hlasoftware.focus.features.profile.domain.repository.IProfileRepository

class UpdateLanguageUseCase(private val repository: IProfileRepository) {
    suspend operator fun invoke(userId: String, language: String) = repository.updateLanguage(userId, language)
}
