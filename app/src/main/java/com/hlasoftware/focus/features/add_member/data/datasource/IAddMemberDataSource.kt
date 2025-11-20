package com.hlasoftware.focus.features.add_member.data.datasource

import com.hlasoftware.focus.features.add_member.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IAddMemberDataSource {
    fun searchUsersByEmail(email: String): Flow<List<User>>
    suspend fun addMemberToWorkgroup(workgroupId: String, userId: String): Result<Unit>
}