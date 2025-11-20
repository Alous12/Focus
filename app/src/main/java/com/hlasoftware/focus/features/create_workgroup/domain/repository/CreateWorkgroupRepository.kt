package com.hlasoftware.focus.features.create_workgroup.domain.repository

import android.net.Uri

interface CreateWorkgroupRepository {
    suspend fun createWorkgroup(
        name: String,
        description: String,
        imageUri: Uri?,
        adminId: String,
        adminName: String,
        memberIds: List<String>
    ): Result<Unit>
}
