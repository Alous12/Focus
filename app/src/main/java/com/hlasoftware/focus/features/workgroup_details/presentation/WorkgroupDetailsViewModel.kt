package com.hlasoftware.focus.features.workgroup_details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.workgroup_details.domain.model.WorkgroupDetails
import com.hlasoftware.focus.features.workgroup_details.domain.usecase.GetWorkgroupDetailsUseCase
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

class WorkgroupDetailsViewModel(
    private val getWorkgroupDetailsUseCase: GetWorkgroupDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkgroupDetailsUiState>(WorkgroupDetailsUiState.Loading)
    val uiState: StateFlow<WorkgroupDetailsUiState> = _uiState

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
}
