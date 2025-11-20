package com.hlasoftware.focus.features.add_member.domain.usecase

import com.hlasoftware.focus.features.add_member.domain.repository.IAddMemberRepository

class AddMemberUseCase(private val repository: IAddMemberRepository) {
    suspend operator fun invoke(workgroupId: String, userId: String): Result<Unit> {
        return repository.addMemberToWorkgroup(workgroupId, userId)
    }
}