package com.hlasoftware.focus.features.add_member.domain.usecase

import com.hlasoftware.focus.features.add_member.domain.model.User
import com.hlasoftware.focus.features.add_member.domain.repository.IAddMemberRepository
import kotlinx.coroutines.flow.Flow

class SearchUsersUseCase(private val repository: IAddMemberRepository) {
    operator fun invoke(email: String): Flow<List<User>> {
        return repository.searchUsersByEmail(email)
    }
}