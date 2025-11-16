package com.hlasoftware.focus.features.home.domain.repository

import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import java.time.LocalDate

interface IHomeRepository {
    suspend fun getActivities(userId: String, date: LocalDate): List<ActivityModel>
    suspend fun deleteActivity(activityId: String)
}
