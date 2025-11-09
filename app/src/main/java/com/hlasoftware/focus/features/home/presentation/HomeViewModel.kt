package com.hlasoftware.focus.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import com.hlasoftware.focus.features.home.domain.usecase.HomeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

// Estado de la UI para Home
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val activities: List<ActivityModel>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val homeUseCase: HomeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

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
                homeUseCase.createActivity(userId, title, description, date, time)
                // Recargamos las actividades para la fecha en que se creó la nueva actividad
                loadHome(userId, date)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error creando la actividad")
            }
        }
    }

    // Función opcional para manejar opciones de cada actividad
    fun onActivityOptionsClicked(activityId: String) {
        // Implementa lógica de opciones si deseas
    }
}
