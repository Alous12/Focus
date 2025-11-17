package com.hlasoftware.focus.features.activity_details.domain.usecase

import com.hlasoftware.focus.features.activity_details.domain.repository.ActivityDetailsRepository
import com.hlasoftware.focus.features.home.domain.model.ActivityModel

class GetActivityDetailsUseCase(private val repository: ActivityDetailsRepository) {
    suspend operator fun invoke(activityId: String): ActivityModel? {
        return repository.getActivityById(activityId)
    }
}
