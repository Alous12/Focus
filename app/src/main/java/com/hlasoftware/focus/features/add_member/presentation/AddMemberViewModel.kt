package com.hlasoftware.focus.features.add_member.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.add_member.domain.model.User
import com.hlasoftware.focus.features.add_member.domain.usecase.AddMemberUseCase
import com.hlasoftware.focus.features.add_member.domain.usecase.SearchUsersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddMemberUiState(
    val searchQuery: String = "",
    val searchResults: List<User> = emptyList(),
    val selectedUsers: Map<String, User> = emptyMap(), // Store map of ID to User
    val isLoading: Boolean = false,
    val error: String? = null,
    val addMemberStatus: AddMemberStatus = AddMemberStatus.Idle
)

enum class AddMemberStatus {
    Idle,
    Loading,
    Success,
    Error
}

class AddMemberViewModel(
    private val searchUsersUseCase: SearchUsersUseCase,
    private val addMemberUseCase: AddMemberUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workgroupId: String? = savedStateHandle.get<String>("workgroupId")
    private val isSelectMode: Boolean = workgroupId == "new"

    private val _uiState = MutableStateFlow(AddMemberUiState())
    val uiState: StateFlow<AddMemberUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState
                .debounce(300)
                .distinctUntilChanged { old, new -> old.searchQuery == new.searchQuery }
                .flatMapLatest { state ->
                    if (state.searchQuery.length >= 2) { // Lowered search threshold
                        searchUsersUseCase(state.searchQuery)
                            .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
                            .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                    } else {
                        MutableStateFlow(emptyList())
                    }
                }
                .collect { users ->
                    _uiState.update { it.copy(isLoading = false, searchResults = users) }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onUserSelected(user: User) {
        _uiState.update { state ->
            val newSelectedUsers = state.selectedUsers.toMutableMap()
            if (newSelectedUsers.containsKey(user.id)) {
                newSelectedUsers.remove(user.id)
            } else {
                newSelectedUsers[user.id] = user
            }
            state.copy(selectedUsers = newSelectedUsers)
        }
    }

    fun onConfirmSelection() {
        if (isSelectMode) {
            // In select mode, just mark as success. The screen will collect the users.
            _uiState.update { it.copy(addMemberStatus = AddMemberStatus.Success) }
        } else {
            addSelectedMembersToWorkgroup()
        }
    }

    private fun addSelectedMembersToWorkgroup() {
        if (workgroupId == null || isSelectMode) return

        viewModelScope.launch {
            _uiState.update { it.copy(addMemberStatus = AddMemberStatus.Loading) }
            try {
                _uiState.value.selectedUsers.keys.forEach { userId ->
                    addMemberUseCase(workgroupId, userId).getOrThrow()
                }
                _uiState.update { it.copy(addMemberStatus = AddMemberStatus.Success) }
            } catch (e: Exception) {
                _uiState.update { it.copy(addMemberStatus = AddMemberStatus.Error, error = e.message) }
            }
        }
    }
}
