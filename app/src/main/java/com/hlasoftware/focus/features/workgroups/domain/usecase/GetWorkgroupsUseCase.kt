package com.hlasoftware.focus.features.workgroups.domain.usecase

import com.hlasoftware.focus.features.workgroups.domain.model.Workgroup
import com.hlasoftware.focus.features.workgroups.domain.repository.WorkgroupRepository
import kotlinx.coroutines.flow.Flow

class GetWorkgroupsUseCase(private val repository: WorkgroupRepository) {
    operator fun invoke(userId: String): Flow<List<Workgroup>> {
        return repository.getWorkgroups(userId)
    }
}
