package com.hlasoftware.focus.features.join_workgroup.domain.usecase

import com.hlasoftware.focus.features.join_workgroup.domain.repository.JoinWorkgroupRepository

class JoinWorkgroupUseCase(private val repository: JoinWorkgroupRepository) {
    suspend operator fun invoke(userId: String, workgroupCode: String): Result<Unit> {
        return repository.joinWorkgroup(userId, workgroupCode)
    }
}
