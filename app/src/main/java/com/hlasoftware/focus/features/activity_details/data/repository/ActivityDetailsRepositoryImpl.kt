package com.hlasoftware.focus.features.activity_details.data.repository

import com.hlasoftware.focus.features.activities.data.repository.ActivityRepository
import com.hlasoftware.focus.features.activity_details.domain.repository.ActivityDetailsRepository
import com.hlasoftware.focus.features.home.domain.model.ActivityModel

class ActivityDetailsRepositoryImpl(private val activityRepository: ActivityRepository) : ActivityDetailsRepository {
    override suspend fun getActivityById(activityId: String): ActivityModel? {
        return activityRepository.getActivityById(activityId)
    }
}
