package com.hlasoftware.focus.features.workgroup_details.domain.usecase

import com.hlasoftware.focus.features.workgroup_details.domain.repository.WorkgroupDetailsRepository

class DeleteWorkgroupUseCase(private val repository: WorkgroupDetailsRepository) {
    suspend operator fun invoke(workgroupId: String): Result<Unit> {
        return repository.deleteWorkgroup(workgroupId)
    }
}
