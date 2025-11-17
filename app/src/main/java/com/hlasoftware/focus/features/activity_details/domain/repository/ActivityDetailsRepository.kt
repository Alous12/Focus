package com.hlasoftware.focus.features.activity_details.domain.repository

import com.hlasoftware.focus.features.home.domain.model.ActivityModel

interface ActivityDetailsRepository {
    suspend fun getActivityById(activityId: String): ActivityModel?
}
