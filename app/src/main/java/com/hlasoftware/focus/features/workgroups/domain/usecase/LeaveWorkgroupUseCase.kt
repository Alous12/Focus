package com.hlasoftware.focus.features.workgroups.domain.usecase

import com.hlasoftware.focus.features.workgroups.domain.repository.WorkgroupRepository

class LeaveWorkgroupUseCase(private val repository: WorkgroupRepository) {
    suspend operator fun invoke(workgroupId: String, userId: String): Result<Unit> {
        return repository.leaveWorkgroup(workgroupId, userId)
    }
}
