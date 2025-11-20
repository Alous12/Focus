package com.hlasoftware.focus.features.workgroup_details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.workgroup_details.domain.model.WorkgroupDetails
import com.hlasoftware.focus.features.workgroup_details.domain.usecase.DeleteWorkgroupUseCase
import com.hlasoftware.focus.features.workgroup_details.domain.usecase.GetWorkgroupDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

sealed class WorkgroupDetailsUiState {
    object Loading : WorkgroupDetailsUiState()
    data class Success(val details: WorkgroupDetails) : WorkgroupDetailsUiState()
    data class Error(val message: String) : WorkgroupDetailsUiState()
}

sealed class DeleteWorkgroupUiState {
    object Idle : DeleteWorkgroupUiState()
    object Loading : DeleteWorkgroupUiState()
    object Success : DeleteWorkgroupUiState()
    data class Error(val message: String) : DeleteWorkgroupUiState()
}

class WorkgroupDetailsViewModel(
    private val getWorkgroupDetailsUseCase: GetWorkgroupDetailsUseCase,
    private val deleteWorkgroupUseCase: DeleteWorkgroupUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkgroupDetailsUiState>(WorkgroupDetailsUiState.Loading)
    val uiState: StateFlow<WorkgroupDetailsUiState> = _uiState

    private val _deleteState = MutableStateFlow<DeleteWorkgroupUiState>(DeleteWorkgroupUiState.Idle)
    val deleteState: StateFlow<DeleteWorkgroupUiState> = _deleteState

    fun loadWorkgroupDetails(workgroupId: String) {
        viewModelScope.launch {
            getWorkgroupDetailsUseCase(workgroupId)
                .onStart { _uiState.value = WorkgroupDetailsUiState.Loading }
                .catch { e -> _uiState.value = WorkgroupDetailsUiState.Error(e.message ?: "Error loading details") }
                .collect { details ->
                    _uiState.value = WorkgroupDetailsUiState.Success(details)
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
}
