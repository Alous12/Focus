package com.hlasoftware.focus.features.home.data.repository

import com.hlasoftware.focus.features.activities.data.repository.ActivityRepository
import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import com.hlasoftware.focus.features.home.domain.repository.IHomeRepository
import java.time.LocalDate

class HomeRepositoryImpl(private val activityRepository: ActivityRepository) : IHomeRepository {
    override suspend fun getActivities(userId: String, date: LocalDate): List<ActivityModel> {
        return activityRepository.getActivities(userId, date)
    }
}
