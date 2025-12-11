package com.hlasoftware.focus.features.routines.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.routines.domain.model.Routine
import com.hlasoftware.focus.features.routines.domain.usecase.DeleteRoutineUseCase
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

class RoutinesViewModel(
    private val getRoutinesUseCase: GetRoutinesUseCase,
    private val deleteRoutineUseCase: DeleteRoutineUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RoutinesUiState>(RoutinesUiState.Loading)
    val uiState: StateFlow<RoutinesUiState> = _uiState.asStateFlow()

    private val _showDeleteConfirmationDialog = MutableStateFlow(false)
    val showDeleteConfirmationDialog = _showDeleteConfirmationDialog.asStateFlow()

    private var routineIdToDelete: String? = null

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

    fun onDeleteRoutineClicked(routineId: String) {
        routineIdToDelete = routineId
        _showDeleteConfirmationDialog.value = true
    }

    fun onConfirmDeleteRoutine() {
        routineIdToDelete?.let { id ->
            viewModelScope.launch {
                deleteRoutineUseCase(id)
                    .onSuccess {
                        // The flow from getRoutinesUseCase should automatically update, 
                        // but if it doesn't we might need to handle it. 
                        // Firestore snapshot listener should handle it.
                        _showDeleteConfirmationDialog.value = false
                        routineIdToDelete = null
                    }
                    .onFailure { e ->
                        // Handle error, maybe show a snackbar or update state
                         _uiState.value = RoutinesUiState.Error(e.message ?: "Error deleting routine")
                        _showDeleteConfirmationDialog.value = false
                        routineIdToDelete = null
                    }
            }
        }
    }

    fun onDismissDeleteRoutine() {
        _showDeleteConfirmationDialog.value = false
        routineIdToDelete = null
    }
}
