package com.hlasoftware.focus.features.workgroup_details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.workgroup_details.domain.model.WorkgroupDetails
import com.hlasoftware.focus.features.workgroup_details.domain.usecase.DeleteWorkgroupUseCase
import com.hlasoftware.focus.features.workgroup_details.domain.usecase.GetWorkgroupDetailsUseCase
import com.hlasoftware.focus.features.workgroup_details.domain.usecase.LeaveWorkgroupUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

sealed class WorkgroupDetailsUiState {
    object Loading : WorkgroupDetailsUiState()
    data class Success(val details: WorkgroupDetails, val isUserAdmin: Boolean) : WorkgroupDetailsUiState() // Added isUserAdmin
    data class Error(val message: String) : WorkgroupDetailsUiState()
}

sealed class WorkgroupActionUiState {
    object Idle : WorkgroupActionUiState()
    object Loading : WorkgroupActionUiState()
    object Success : WorkgroupActionUiState()
    data class Error(val message: String) : WorkgroupActionUiState()
}

class WorkgroupDetailsViewModel(
    private val getWorkgroupDetailsUseCase: GetWorkgroupDetailsUseCase,
    private val deleteWorkgroupUseCase: DeleteWorkgroupUseCase,
    private val leaveWorkgroupUseCase: LeaveWorkgroupUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkgroupDetailsUiState>(WorkgroupDetailsUiState.Loading)
    val uiState: StateFlow<WorkgroupDetailsUiState> = _uiState

    private val _actionState = MutableStateFlow<WorkgroupActionUiState>(WorkgroupActionUiState.Idle)
    val actionState: StateFlow<WorkgroupActionUiState> = _actionState

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

    fun deleteWorkgroup(workgroupId: String) {
        viewModelScope.launch {
            _actionState.value = WorkgroupActionUiState.Loading
            deleteWorkgroupUseCase(workgroupId)
                .onSuccess {
                    _actionState.value = WorkgroupActionUiState.Success
                }
                .onFailure {
                    _actionState.value = WorkgroupActionUiState.Error(it.message ?: "Error deleting workgroup")
                }
        }
    }

    fun leaveWorkgroup(workgroupId: String, userId: String) {
        viewModelScope.launch {
            _actionState.value = WorkgroupActionUiState.Loading
            leaveWorkgroupUseCase(workgroupId, userId)
                .onSuccess {
                    _actionState.value = WorkgroupActionUiState.Success
                }
                .onFailure {
                    _actionState.value = WorkgroupActionUiState.Error(it.message ?: "Error leaving workgroup")
                }
        }
    }

    fun resetActionState() {
        _actionState.value = WorkgroupActionUiState.Idle
    }
}
