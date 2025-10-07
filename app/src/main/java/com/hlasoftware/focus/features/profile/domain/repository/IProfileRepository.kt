package com.hlasoftware.focus.features.profile.domain.repository

import com.hlasoftware.focus.features.profile.domain.model.ProfileModel

interface IProfileRepository {
    fun fetchData(): Result<ProfileModel>
}