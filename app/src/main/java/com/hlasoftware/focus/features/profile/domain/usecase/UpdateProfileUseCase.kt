package com.hlasoftware.focus.features.profile.domain.usecase

import android.net.Uri
import com.hlasoftware.focus.features.profile.domain.repository.IProfileRepository

class UpdateProfileUseCase(private val repository: IProfileRepository) {
    suspend fun updateSummary(userId: String, summary: String) = repository.updateSummary(userId, summary)
    suspend fun updateProfilePicture(userId: String, imageUri: Uri) = repository.updateProfilePicture(userId, imageUri)
}