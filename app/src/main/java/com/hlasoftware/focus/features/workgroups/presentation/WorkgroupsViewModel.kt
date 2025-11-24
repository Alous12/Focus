package com.hlasoftware.focus.features.workgroups.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.workgroups.domain.model.Workgroup
import com.hlasoftware.focus.features.workgroups.domain.usecase.DeleteWorkgroupUseCase
import com.hlasoftware.focus.features.workgroups.domain.usecase.GetWorkgroupsUseCase
import com.hlasoftware.focus.features.workgroups.domain.usecase.LeaveWorkgroupUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

sealed class WorkgroupsUiState {
    object Loading : WorkgroupsUiState()
    data class Success(val workgroups: List<Workgroup>) : WorkgroupsUiState()
    data class Error(val message: String) : WorkgroupsUiState()
}

sealed class DeleteWorkgroupUiState {
    object Idle : DeleteWorkgroupUiState()
    object Loading : DeleteWorkgroupUiState()
    object Success : DeleteWorkgroupUiState()
    data class Error(val message: String) : DeleteWorkgroupUiState()
}

sealed class LeaveWorkgroupUiState {
    object Idle : LeaveWorkgroupUiState()
    object Loading : LeaveWorkgroupUiState()
    object Success : LeaveWorkgroupUiState()
    data class Error(val message: String) : LeaveWorkgroupUiState()
}

class WorkgroupsViewModel(
    private val getWorkgroupsUseCase: GetWorkgroupsUseCase,
    private val deleteWorkgroupUseCase: DeleteWorkgroupUseCase,
    private val leaveWorkgroupUseCase: LeaveWorkgroupUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkgroupsUiState>(WorkgroupsUiState.Loading)
    val uiState: StateFlow<WorkgroupsUiState> = _uiState

    private val _deleteState = MutableStateFlow<DeleteWorkgroupUiState>(DeleteWorkgroupUiState.Idle)
    val deleteState: StateFlow<DeleteWorkgroupUiState> = _deleteState

    private val _leaveState = MutableStateFlow<LeaveWorkgroupUiState>(LeaveWorkgroupUiState.Idle)
    val leaveState: StateFlow<LeaveWorkgroupUiState> = _leaveState

    fun listenToWorkgroups(userId: String) {
        viewModelScope.launch {
            getWorkgroupsUseCase(userId)
                .onStart { _uiState.value = WorkgroupsUiState.Loading }
                .catch { e -> _uiState.value = WorkgroupsUiState.Error(e.message ?: "Error escuchando los grupos de trabajo") }
                .collect { workgroups ->
                    _uiState.value = WorkgroupsUiState.Success(workgroups)
                }
        }
    }

    fun deleteWorkgroup(workgroupId: String) {
        viewModelScope.launch {
            _deleteState.value = DeleteWorkgroupUiState.Loading
            deleteWorkgroupUseCase(workgroupId)
                .onSuccess {
                    _deleteState.value = DeleteWorkgroupUiState.Success
                }
                .onFailure {
                    _deleteState.value = DeleteWorkgroupUiState.Error(it.message ?: "Error deleting workgroup")
                }
        }
    }
    
    fun resetDeleteState() {
        _deleteState.value = DeleteWorkgroupUiState.Idle
    }

    fun leaveWorkgroup(workgroupId: String, userId: String) {
        viewModelScope.launch {
            _leaveState.value = LeaveWorkgroupUiState.Loading
            leaveWorkgroupUseCase(workgroupId, userId)
                .onSuccess {
                    _leaveState.value = LeaveWorkgroupUiState.Success
                }
                .onFailure {
                    _leaveState.value = LeaveWorkgroupUiState.Error(it.message ?: "Error leaving workgroup")
                }
        }
    }

    fun resetLeaveState() {
        _leaveState.value = LeaveWorkgroupUiState.Idle
    }
}
