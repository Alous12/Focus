package com.hlasoftware.focus.features.activities.data.repository

import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import java.time.LocalDate
import java.time.LocalTime

interface ActivityRepository {
    suspend fun getActivities(userId: String, date: LocalDate): List<ActivityModel>
    suspend fun getActivitiesForMonth(userId: String, year: Int, month: Int): List<ActivityModel>
    suspend fun createActivity(
        userId: String,
        title: String,
        description: String,
        date: LocalDate,
        time: LocalTime?
    ): String
    suspend fun deleteActivity(activityId: String)
    suspend fun getActivityById(activityId: String): ActivityModel?
}
