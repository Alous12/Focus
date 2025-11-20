package com.hlasoftware.focus.features.add_member.data.repository

import com.hlasoftware.focus.features.add_member.data.datasource.IAddMemberDataSource
import com.hlasoftware.focus.features.add_member.domain.model.User
import com.hlasoftware.focus.features.add_member.domain.repository.IAddMemberRepository
import kotlinx.coroutines.flow.Flow

class AddMemberRepositoryImpl(private val dataSource: IAddMemberDataSource) : IAddMemberRepository {

    override fun searchUsersByEmail(email: String): Flow<List<User>> {
        return dataSource.searchUsersByEmail(email)
    }

    override suspend fun addMemberToWorkgroup(workgroupId: String, userId: String): Result<Unit> {
        return dataSource.addMemberToWorkgroup(workgroupId, userId)
    }
}