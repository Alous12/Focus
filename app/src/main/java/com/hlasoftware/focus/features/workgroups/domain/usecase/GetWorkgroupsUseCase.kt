package com.hlasoftware.focus.features.workgroups.domain.usecase

import com.hlasoftware.focus.features.workgroups.domain.model.Workgroup
import com.hlasoftware.focus.features.workgroups.domain.repository.WorkgroupRepository

class GetWorkgroupsUseCase(private val repository: WorkgroupRepository) {
    suspend operator fun invoke(userId: String): List<Workgroup> {
        return repository.getWorkgroups(userId)
    }
}
