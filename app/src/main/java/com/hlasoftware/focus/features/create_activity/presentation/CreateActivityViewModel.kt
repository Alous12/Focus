package com.hlasoftware.focus.features.create_activity.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hlasoftware.focus.features.activities.data.repository.ActivityRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class CreateActivityViewModel(
    private val activityRepository: ActivityRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    fun createActivity(
        name: String,
        description: String,
        date: LocalDate,
        time: LocalTime?
    ) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            activityRepository.createActivity(
                userId = userId,
                title = name,
                description = description,
                date = date,
                time = time
            )
        }
    }
}
