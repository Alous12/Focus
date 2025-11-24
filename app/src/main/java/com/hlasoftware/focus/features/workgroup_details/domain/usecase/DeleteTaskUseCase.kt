package com.hlasoftware.focus.features.workgroup_details.domain.usecase

import com.hlasoftware.focus.features.workgroup_details.domain.repository.WorkgroupDetailsRepository

class DeleteTaskUseCase(private val repository: WorkgroupDetailsRepository) {
    suspend operator fun invoke(taskId: String): Result<Unit> {
        return repository.deleteTask(taskId)
    }
}
