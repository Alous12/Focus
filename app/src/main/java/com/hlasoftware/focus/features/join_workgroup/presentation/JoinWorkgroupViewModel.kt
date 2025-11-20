package com.hlasoftware.focus.features.join_workgroup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.join_workgroup.domain.usecase.JoinWorkgroupUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class JoinWorkgroupUiState {
    object Idle : JoinWorkgroupUiState()
    object Loading : JoinWorkgroupUiState()
    object Success : JoinWorkgroupUiState()
    data class Error(val message: String) : JoinWorkgroupUiState()
}

class JoinWorkgroupViewModel(private val joinWorkgroupUseCase: JoinWorkgroupUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<JoinWorkgroupUiState>(JoinWorkgroupUiState.Idle)
    val uiState: StateFlow<JoinWorkgroupUiState> = _uiState

    fun joinWorkgroup(userId: String, workgroupCode: String) {
        viewModelScope.launch {
            _uiState.value = JoinWorkgroupUiState.Loading
            joinWorkgroupUseCase(userId, workgroupCode)
                .onSuccess {
                    _uiState.value = JoinWorkgroupUiState.Success
                }
                .onFailure {
                    _uiState.value = JoinWorkgroupUiState.Error(it.message ?: "Error joining workgroup")
                }
        }
    }
}
