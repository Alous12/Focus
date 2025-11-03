package com.hlasoftware.focus.features.home.domain.usecase

import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import com.hlasoftware.focus.features.home.domain.repository.IHomeRepository
import java.time.LocalDate

class HomeUseCase(private val homeRepository: IHomeRepository) {
    suspend operator fun invoke(userId: String, date: LocalDate): List<ActivityModel> {
        return homeRepository.getActivities(userId, date)
    }
}
