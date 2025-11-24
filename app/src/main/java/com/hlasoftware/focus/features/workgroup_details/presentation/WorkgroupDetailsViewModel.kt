package com.hlasoftware.focus.features.workgroup_details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.workgroup_details.domain.model.WorkgroupDetails
import com.hlasoftware.focus.features.workgroup_details.domain.usecase.DeleteTaskUseCase
import com.hlasoftware.focus.features.workgroup_details.domain.usecase.GetWorkgroupDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

sealed class WorkgroupDetailsUiState {
    object Loading : WorkgroupDetailsUiState()
    data class Success(val details: WorkgroupDetails, val isUserAdmin: Boolean) : WorkgroupDetailsUiState()
    data class Error(val message: String) : WorkgroupDetailsUiState()
}

sealed class DeleteTaskUiState {
    object Idle : DeleteTaskUiState()
    object Loading : DeleteTaskUiState()
    object Success : DeleteTaskUiState()
    data class Error(val message: String) : DeleteTaskUiState()
}

class WorkgroupDetailsViewModel(
    private val getWorkgroupDetailsUseCase: GetWorkgroupDetailsUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkgroupDetailsUiState>(WorkgroupDetailsUiState.Loading)
    val uiState: StateFlow<WorkgroupDetailsUiState> = _uiState

    private val _deleteTaskState = MutableStateFlow<DeleteTaskUiState>(DeleteTaskUiState.Idle)
    val deleteTaskState: StateFlow<DeleteTaskUiState> = _deleteTaskState

    fun loadWorkgroupDetails(workgroupId: String, userId: String) {
        viewModelScope.launch {
            getWorkgroupDetailsUseCase(workgroupId)
                .onStart { _uiState.value = WorkgroupDetailsUiState.Loading }
                .catch { e -> _uiState.value = WorkgroupDetailsUiState.Error(e.message ?: "Error loading details") }
                .collect { details ->
                    val isUserAdmin = details.workgroup.admin == userId
                    _uiState.value = WorkgroupDetailsUiState.Success(details, isUserAdmin)
                }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _deleteTaskState.value = DeleteTaskUiState.Loading
            deleteTaskUseCase(taskId)
                .onSuccess {
                    _deleteTaskState.value = DeleteTaskUiState.Success
                }
                .onFailure {
                    _deleteTaskState.value = DeleteTaskUiState.Error(it.message ?: "Error deleting task")
                }
        }
    }

    fun resetDeleteTaskState() {
        _deleteTaskState.value = DeleteTaskUiState.Idle
    }
}
