package com.hlasoftware.focus.features.home.domain.usecase

import com.hlasoftware.focus.features.activities.data.repository.ActivityRepository
import com.hlasoftware.focus.features.home.domain.model.HomeModel
import java.time.LocalDate
import java.time.LocalTime

class HomeUseCase(private val activityRepository: ActivityRepository) {
    suspend operator fun invoke(userId: String, date: LocalDate): HomeModel {
        val activities = activityRepository.getActivities(userId, date)
        return HomeModel(upcomingActivities = activities)
    }

    suspend fun createActivity(
        userId: String,
        title: String,
        description: String,
        date: LocalDate,
        time: LocalTime?
    ): String {
        return activityRepository.createActivity(userId, title, description, date, time)
    }

    suspend fun deleteActivity(activityId: String) {
        activityRepository.deleteActivity(activityId)
    }
}
