package com.hlasoftware.focus.features.home.domain.usecase

import com.hlasoftware.focus.features.activities.data.repository.ActivityRepository
import com.hlasoftware.focus.features.home.domain.model.HomeModel
import java.time.LocalDate

class HomeUseCase(private val activityRepository: ActivityRepository) {
    suspend operator fun invoke(userId: String, date: LocalDate): HomeModel {
        val activities = activityRepository.getActivities(userId, date)
        return HomeModel(upcomingActivities = activities)
    }
}
