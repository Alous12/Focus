package com.hlasoftware.focus.features.add_task.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.add_task.domain.model.Task
import com.hlasoftware.focus.features.add_task.domain.usecase.AddTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AddTaskUiState {
    object Idle : AddTaskUiState()
    object Loading : AddTaskUiState()
    object Success : AddTaskUiState()
    data class Error(val message: String) : AddTaskUiState()
}

class AddTaskViewModel(private val addTaskUseCase: AddTaskUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<AddTaskUiState>(AddTaskUiState.Idle)
    val uiState: StateFlow<AddTaskUiState> = _uiState

    fun addTask(task: Task) {
        viewModelScope.launch {
            _uiState.value = AddTaskUiState.Loading
            addTaskUseCase(task)
                .onSuccess {
                    _uiState.value = AddTaskUiState.Success
                }
                .onFailure {
                    _uiState.value = AddTaskUiState.Error(it.message ?: "Error creating task")
                }
        }
    }

    fun resetState() {
        _uiState.value = AddTaskUiState.Idle
    }
}
