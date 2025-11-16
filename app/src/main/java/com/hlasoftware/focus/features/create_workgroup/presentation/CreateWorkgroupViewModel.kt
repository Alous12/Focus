package com.hlasoftware.focus.features.create_workgroup.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.create_workgroup.domain.usecase.CreateWorkgroupUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CreateWorkgroupUiState {
    object Idle : CreateWorkgroupUiState()
    object Loading : CreateWorkgroupUiState()
    object Success : CreateWorkgroupUiState()
    data class Error(val message: String) : CreateWorkgroupUiState()
}

class CreateWorkgroupViewModel(private val createWorkgroupUseCase: CreateWorkgroupUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateWorkgroupUiState>(CreateWorkgroupUiState.Idle)
    val uiState: StateFlow<CreateWorkgroupUiState> = _uiState

    fun createWorkgroup(
        name: String,
        description: String,
        imageUri: Uri?,
        adminId: String
    ) {
        viewModelScope.launch {
            _uiState.value = CreateWorkgroupUiState.Loading
            createWorkgroupUseCase(name, description, imageUri, adminId)
                .onSuccess {
                    _uiState.value = CreateWorkgroupUiState.Success
                }
                .onFailure {
                    _uiState.value = CreateWorkgroupUiState.Error(it.message ?: "Error creando el grupo de trabajo")
                }
        }
    }

    fun resetState() {
        _uiState.value = CreateWorkgroupUiState.Idle
    }
}
