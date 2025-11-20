package com.hlasoftware.focus.features.workgroups.data.repository

import com.hlasoftware.focus.features.workgroups.domain.model.Workgroup

interface WorkgroupRepository {
    suspend fun getWorkgroups(userId: String): List<Workgroup>
}
