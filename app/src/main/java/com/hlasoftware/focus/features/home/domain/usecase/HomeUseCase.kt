package com.hlasoftware.focus.features.home.domain.usecase // Make sure this package path is correct

import com.hlasoftware.focus.features.home.domain.model.HomeModel
import com.hlasoftware.focus.features.home.domain.repository.IHomeRepository
import java.time.LocalDate


class HomeUseCase(
    private val repository: IHomeRepository
) {
    suspend operator fun invoke(userId: String, date: LocalDate): HomeModel {
        return repository.getHomeData(userId, date)
    }
}
