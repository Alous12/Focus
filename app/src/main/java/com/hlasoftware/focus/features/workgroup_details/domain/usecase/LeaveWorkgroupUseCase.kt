package com.hlasoftware.focus.features.workgroup_details.domain.usecase

import com.hlasoftware.focus.features.workgroup_details.domain.repository.WorkgroupDetailsRepository

class LeaveWorkgroupUseCase(private val repository: WorkgroupDetailsRepository) {
    suspend operator fun invoke(workgroupId: String, userId: String): Result<Unit> {
        return repository.leaveWorkgroup(workgroupId, userId)
    }
}
