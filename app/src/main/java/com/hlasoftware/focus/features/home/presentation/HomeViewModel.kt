package com.hlasoftware.focus.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import com.hlasoftware.focus.features.home.domain.usecase.HomeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

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

                val homeModel = homeUseCase(userId, date)
                _uiState.value = HomeUiState.Success(homeModel.upcomingActivities)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error cargando actividades")
            }
        }
    }

    // Función opcional para manejar opciones de cada actividad
    fun onActivityOptionsClicked(activityId: String) {
        // Implementa lógica de opciones si deseas
    }
}
