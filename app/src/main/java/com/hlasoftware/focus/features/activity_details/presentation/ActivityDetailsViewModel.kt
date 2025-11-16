package com.hlasoftware.focus.features.activity_details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.activity_details.domain.usecase.GetActivityDetailsUseCase
import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ActivityDetailsUiState {
    object Loading : ActivityDetailsUiState()
    data class Success(val activity: ActivityModel) : ActivityDetailsUiState()
    data class Error(val message: String) : ActivityDetailsUiState()
}

class ActivityDetailsViewModel(private val getActivityDetailsUseCase: GetActivityDetailsUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow<ActivityDetailsUiState>(ActivityDetailsUiState.Loading)
    val uiState: StateFlow<ActivityDetailsUiState> = _uiState

    fun loadActivityDetails(activityId: String) {
        viewModelScope.launch {
            _uiState.value = ActivityDetailsUiState.Loading
            try {
                val activity = getActivityDetailsUseCase(activityId)
                if (activity != null) {
                    _uiState.value = ActivityDetailsUiState.Success(activity)
                } else {
                    _uiState.value = ActivityDetailsUiState.Error("Activity not found")
                }
            } catch (e: Exception) {
                _uiState.value = ActivityDetailsUiState.Error(e.message ?: "Error loading activity details")
            }
        }
    }
}
