package com.hlasoftware.focus.features.home.domain.usecase

import com.hlasoftware.focus.features.activities.data.repository.ActivityRepository
import com.hlasoftware.focus.features.home.domain.model.HomeModel
import java.time.LocalDate

class HomeUseCase(private val activityRepository: ActivityRepository) {
    suspend operator fun invoke(userId: String, date: LocalDate): HomeModel {
        val activities = activityRepository.getActivities(userId, date)
        // You can add more logic here if needed, for example, sorting activities
        return HomeModel(upcomingActivities = activities)
    }
}
