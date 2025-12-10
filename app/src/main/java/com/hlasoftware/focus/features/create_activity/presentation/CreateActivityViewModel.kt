package com.hlasoftware.focus.features.create_activity.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hlasoftware.focus.features.activities.data.repository.ActivityRepository
import com.hlasoftware.focus.features.notifications.NotificationScheduler
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class CreateActivityViewModel(
    private val activityRepository: ActivityRepository,
    private val auth: FirebaseAuth,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    fun createActivity(
        name: String,
        description: String,
        date: LocalDate,
        time: LocalTime?
    ) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            val activityId = activityRepository.createActivity(
                userId = userId,
                title = name,
                description = description,
                date = date,
                time = time
            )

            if (time != null && activityId.isNotEmpty()) {
                val dateTime = LocalDateTime.of(date, time)
                val notificationTime = dateTime.minusMinutes(5)
                val millis = notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                if (millis > System.currentTimeMillis()) {
                    notificationScheduler.scheduleNotification(
                        activityId = activityId,
                        title = "Actividad por comenzar",
                        message = "La actividad '$name' comienza en 5 minutos.",
                        scheduledTimeMillis = millis
                    )
                }
            }
        }
    }
}
