package com.hlasoftware.focus.features.activities.data.repository

import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import java.time.LocalDate

interface ActivityRepository {
    suspend fun getActivities(userId: String, date: LocalDate): List<ActivityModel>
    suspend fun addActivity(activity: ActivityModel)
}
