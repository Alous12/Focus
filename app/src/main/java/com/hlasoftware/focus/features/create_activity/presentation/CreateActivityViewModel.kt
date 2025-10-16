package com.hlasoftware.focus.features.create_activity.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hlasoftware.focus.features.activities.data.repository.ActivityRepository
import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CreateActivityViewModel(
    private val activityRepository: ActivityRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    fun createActivity(
        name: String,
        description: String,
        date: LocalDate,
        time: LocalTime? // Time is optional
    ) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            val activity = ActivityModel(
                userId = userId,
                title = name,
                description = description,
                date = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                startTime = time?.format(DateTimeFormatter.ofPattern("HH:mm")),

            )

            activityRepository.addActivity(activity)
        }
    }
}
