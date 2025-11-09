package com.hlasoftware.focus.features.profile.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.profile.domain.usecase.GetProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileUseCase: GetProfileUseCase
) : ViewModel() {

    sealed class ProfileUiState {
        object Init : ProfileUiState()
        object Loading : ProfileUiState()
        data class Error(val message: String) : ProfileUiState()
        data class Success(val profile: ProfileModel) : ProfileUiState()
    }

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Init)
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun showProfile(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ProfileUiState.Loading
            // El UseCase ahora requiere un userId
            val resultProfile = profileUseCase.invoke(userId)
            resultProfile.fold(
                onSuccess = {
                    _state.value = ProfileUiState.Success(it)
                },
                onFailure = {
                    _state.value = ProfileUiState.Error(it.message ?: "Ocurrió un error desconocido")
                }
            )
        }
    }
}