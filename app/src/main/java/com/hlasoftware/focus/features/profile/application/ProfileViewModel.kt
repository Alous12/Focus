package com.hlasoftware.focus.features.profile.application

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hlasoftware.focus.features.activities.data.repository.ActivityRepository
import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import com.hlasoftware.focus.features.posts.domain.model.PostModel
import com.hlasoftware.focus.features.posts.domain.usecase.CreatePostUseCase
import com.hlasoftware.focus.features.posts.domain.usecase.DeletePostUseCase
import com.hlasoftware.focus.features.posts.domain.usecase.GetPostsUseCase
import com.hlasoftware.focus.features.posts.domain.usecase.UpdatePostUseCase
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.profile.domain.usecase.GetProfileUseCase
import com.hlasoftware.focus.features.profile.domain.usecase.UpdateProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val activityRepository: ActivityRepository,
    private val getPostsUseCase: GetPostsUseCase,
    private val createPostUseCase: CreatePostUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
    private val deletePostUseCase: DeletePostUseCase,
) : ViewModel() {

    sealed class ProfileUiState {
        object Init : ProfileUiState()
        object Loading : ProfileUiState()
        data class Error(val message: String) : ProfileUiState()
        data class Success(val profile: ProfileModel) : ProfileUiState()
    }

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Init)
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _activities = MutableStateFlow<List<ActivityModel>>(emptyList())
    val activities: StateFlow<List<ActivityModel>> = _activities.asStateFlow()

    private val _posts = MutableStateFlow<List<PostModel>>(emptyList())
    val posts: StateFlow<List<PostModel>> = _posts.asStateFlow()

    fun showProfile(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ProfileUiState.Loading
            val resultProfile = getProfileUseCase.invoke(userId)
            resultProfile.fold(
                onSuccess = { 
                    _state.value = ProfileUiState.Success(it)
                    loadPosts(userId)
                 },
                onFailure = { _state.value = ProfileUiState.Error(it.message ?: "Ocurrió un error desconocido") }
            )
        }
    }

    fun loadActivitiesForMonth(userId: String, year: Int, month: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _activities.value = activityRepository.getActivitiesForMonth(userId, year, month)
        }
    }

    fun updateSummary(userId: String, summary: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateProfileUseCase.updateSummary(userId, summary).onSuccess {
                showProfile(userId) // Recargar perfil
            }
        }
    }

    fun updateProfilePicture(userId: String, imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            updateProfileUseCase.updateProfilePicture(userId, imageUri).onSuccess {
                showProfile(userId) // Recargar perfil
            }
        }
    }

    private fun loadPosts(userId: String) {
        viewModelScope.launch {
            getPostsUseCase(userId).collect {
                _posts.value = it
            }
        }
    }

    fun createPost(userId: String, text: String, imageUri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            createPostUseCase(userId, text, imageUri)
        }
    }

    fun updatePost(postId: String, text: String, imageUri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            updatePostUseCase(postId, text, imageUri)
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deletePostUseCase(postId)
        }
    }
}