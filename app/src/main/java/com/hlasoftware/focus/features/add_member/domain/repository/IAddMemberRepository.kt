package com.hlasoftware.focus.features.add_member.domain.repository

import com.hlasoftware.focus.features.add_member.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IAddMemberRepository {
    fun searchUsersByEmail(email: String): Flow<List<User>>
    suspend fun addMemberToWorkgroup(workgroupId: String, userId: String): Result<Unit>
}