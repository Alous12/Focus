package com.hlasoftware.focus.features.profile.domain.repository

import android.net.Uri
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel

interface IProfileRepository {
    suspend fun fetchData(userId: String): Result<ProfileModel>
    suspend fun updateSummary(userId: String, summary: String): Result<Unit>
    suspend fun updateProfilePicture(userId: String, imageUri: Uri): Result<Unit>
}
