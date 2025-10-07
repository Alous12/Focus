package com.hlasoftware.focus.features.profile.data.repository

import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.profile.domain.repository.IProfileRepository

class ProfileRepository: IProfileRepository {
    override fun fetchData(): Result<ProfileModel> {
        return Result.success(
            ProfileModel(
                name = "Homero J. Simpson",
                email = "homero.simpson@springfieldmail.com",
                //cellphone = "+1 (939) 555‑7422",
                pathUrl = "https://www.viaempresa.cat/uploads/s1/43/99/69/homer.jpg",
                summary = "Ciudadano de Springfield y dedicado inspector de seguridad en la Planta Nuclear."
            )
        )
    }
}