package com.hlasoftware.focus.features.join_workgroup.domain.repository

interface JoinWorkgroupRepository {
    suspend fun joinWorkgroup(userId: String, workgroupCode: String): Result<Unit>
}
