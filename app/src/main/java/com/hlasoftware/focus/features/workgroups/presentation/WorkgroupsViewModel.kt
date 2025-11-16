package com.hlasoftware.focus.features.workgroups.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.workgroups.domain.model.Workgroup
import com.hlasoftware.focus.features.workgroups.domain.usecase.GetWorkgroupsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WorkgroupsUiState {
    object Loading : WorkgroupsUiState()
    data class Success(val workgroups: List<Workgroup>) : WorkgroupsUiState()
    data class Error(val message: String) : WorkgroupsUiState()
}

class WorkgroupsViewModel(private val getWorkgroupsUseCase: GetWorkgroupsUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkgroupsUiState>(WorkgroupsUiState.Loading)
    val uiState: StateFlow<WorkgroupsUiState> = _uiState

    fun loadWorkgroups(userId: String) {
        viewModelScope.launch {
            _uiState.value = WorkgroupsUiState.Loading
            try {
                val workgroups = getWorkgroupsUseCase(userId)
                _uiState.value = WorkgroupsUiState.Success(workgroups)
            } catch (e: Exception) {
                _uiState.value = WorkgroupsUiState.Error(e.message ?: "Error loading workgroups")
            }
        }
    }
}
