package com.hlasoftware.focus.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import com.hlasoftware.focus.features.home.domain.usecase.HomeUseCase
import com.hlasoftware.focus.features.notifications.NotificationScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

// Estado de la UI para Home
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val activities: List<ActivityModel>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val homeUseCase: HomeUseCase,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _showDeleteConfirmationDialog = MutableStateFlow(false)
    val showDeleteConfirmationDialog = _showDeleteConfirmationDialog.asStateFlow()

    private var activityIdToDelete: String? = null

    fun loadHome(userId: String, date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val activities = homeUseCase(userId, date)
                _uiState.value = HomeUiState.Success(activities.upcomingActivities)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error cargando actividades")
            }
        }
    }

    fun createActivity(
        userId: String,
        title: String,
        description: String,
        date: LocalDate,
        time: LocalTime?
    ) {
        viewModelScope.launch {
            try {
                val activityId = homeUseCase.createActivity(userId, title, description, date, time)

                if (time != null && activityId.isNotEmpty()) {
                    val dateTime = LocalDateTime.of(date, time)
                    val notificationTime = dateTime.minusMinutes(5)
                    val millis = notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                    if (millis > System.currentTimeMillis()) {
                        notificationScheduler.scheduleNotification(
                            activityId = activityId,
                            title = "Actividad por comenzar",
                            message = "La actividad '$title' comienza en 5 minutos.",
                            scheduledTimeMillis = millis
                        )
                    }
                }
                
                // Recargamos las actividades para la fecha en que se creó la nueva actividad
                loadHome(userId, date)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error creando la actividad")
            }
        }
    }

    fun onActivityOptionsClicked(activityId: String) {
        // Not used yet, will be used to show the dropdown
    }

    fun onDeleteActivityClicked(activityId: String) {
        activityIdToDelete = activityId
        _showDeleteConfirmationDialog.value = true
    }

    fun onConfirmDeleteActivity(userId: String, date: LocalDate) {
        activityIdToDelete?.let {
            viewModelScope.launch {
                try {
                    homeUseCase.deleteActivity(it)
                    notificationScheduler.cancelNotification(it)
                    loadHome(userId, date)
                } catch (e: Exception) {
                    _uiState.value = HomeUiState.Error(e.message ?: "Error eliminando la actividad")
                } finally {
                    _showDeleteConfirmationDialog.value = false
                    activityIdToDelete = null
                }
            }
        }
    }

    fun onDismissDeleteActivity() {
        _showDeleteConfirmationDialog.value = false
        activityIdToDelete = null
    }
}
