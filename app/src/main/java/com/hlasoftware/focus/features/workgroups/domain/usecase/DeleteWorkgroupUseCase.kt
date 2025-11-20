package com.hlasoftware.focus.features.workgroups.domain.usecase

import com.hlasoftware.focus.features.workgroups.domain.repository.WorkgroupRepository

class DeleteWorkgroupUseCase(private val repository: WorkgroupRepository) {
    suspend operator fun invoke(workgroupId: String): Result<Unit> {
        return repository.deleteWorkgroup(workgroupId)
    }
}
