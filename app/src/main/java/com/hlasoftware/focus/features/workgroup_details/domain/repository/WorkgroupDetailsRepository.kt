package com.hlasoftware.focus.features.workgroup_details.domain.repository

import com.hlasoftware.focus.features.workgroup_details.domain.model.WorkgroupDetails
import kotlinx.coroutines.flow.Flow

interface WorkgroupDetailsRepository {
    fun getWorkgroupDetails(workgroupId: String): Flow<WorkgroupDetails>
    suspend fun deleteTask(taskId: String): Result<Unit>
}
