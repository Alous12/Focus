package com.hlasoftware.focus.features.workgroups.domain.repository

import com.hlasoftware.focus.features.workgroups.domain.model.Workgroup
import kotlinx.coroutines.flow.Flow

interface WorkgroupRepository {
    fun getWorkgroups(userId: String): Flow<List<Workgroup>>
    suspend fun deleteWorkgroup(workgroupId: String): Result<Unit>
}
