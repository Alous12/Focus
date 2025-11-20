package com.hlasoftware.focus.features.workgroup_details.domain.usecase

import com.hlasoftware.focus.features.workgroup_details.domain.model.WorkgroupDetails
import com.hlasoftware.focus.features.workgroup_details.domain.repository.WorkgroupDetailsRepository
import kotlinx.coroutines.flow.Flow

class GetWorkgroupDetailsUseCase(private val repository: WorkgroupDetailsRepository) {
    operator fun invoke(workgroupId: String): Flow<WorkgroupDetails> {
        return repository.getWorkgroupDetails(workgroupId)
    }
}
