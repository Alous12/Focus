package com.hlasoftware.focus.features.routines.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.routines.domain.model.Routine
import com.hlasoftware.focus.features.routines.domain.usecase.GetRoutinesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class RoutinesUiState {
    object Loading : RoutinesUiState()
    data class Success(val routines: List<Routine>) : RoutinesUiState()
    data class Error(val message: String) : RoutinesUiState()
}

class RoutinesViewModel(private val getRoutinesUseCase: GetRoutinesUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<RoutinesUiState>(RoutinesUiState.Loading)
    val uiState: StateFlow<RoutinesUiState> = _uiState.asStateFlow()

    init {
        loadRoutines()
    }

    private fun loadRoutines() {
        viewModelScope.launch {
            getRoutinesUseCase()
                .catch { e -> _uiState.value = RoutinesUiState.Error(e.message ?: "Unknown Error") }
                .collect { routines ->
                    _uiState.value = RoutinesUiState.Success(routines)
                }
        }
    }
}
