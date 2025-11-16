package com.hlasoftware.focus.features.add_routines.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.add_routines.domain.usecase.AddRoutineUseCase
import com.hlasoftware.focus.features.routines.domain.model.Routine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AddRoutineUiState {
    object Idle : AddRoutineUiState()
    object Loading : AddRoutineUiState()
    object Success : AddRoutineUiState()
    data class Error(val message: String) : AddRoutineUiState()
}

class AddRoutineViewModel(private val addRoutineUseCase: AddRoutineUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<AddRoutineUiState>(AddRoutineUiState.Idle)
    val uiState: StateFlow<AddRoutineUiState> = _uiState.asStateFlow()

    fun createRoutine(
        name: String,
        description: String?,
        days: List<String>,
        startTime: String?,
        endTime: String?,
        color: Int
    ) {
        if (name.isBlank()) {
            _uiState.value = AddRoutineUiState.Error("El nombre no puede estar vacío")
            return
        }
        if (days.isEmpty()) {
            _uiState.value = AddRoutineUiState.Error("Debes seleccionar al menos un día")
            return
        }

        viewModelScope.launch {
            _uiState.value = AddRoutineUiState.Loading
            val routine = Routine(
                name = name,
                description = description?.takeIf { it.isNotBlank() },
                days = days,
                startTime = startTime,
                endTime = endTime,
                color = color
            )
            addRoutineUseCase(routine)
                .onSuccess { _uiState.value = AddRoutineUiState.Success }
                .onFailure { e -> _uiState.value = AddRoutineUiState.Error(e.message ?: "No se pudo crear la rutina") }
        }
    }
}
