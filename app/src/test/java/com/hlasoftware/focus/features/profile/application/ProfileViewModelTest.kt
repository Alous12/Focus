package com.hlasoftware.focus.features.profile.application

import com.hlasoftware.focus.features.activities.data.repository.ActivityRepository
import com.hlasoftware.focus.features.posts.domain.usecase.CreatePostUseCase
import com.hlasoftware.focus.features.posts.domain.usecase.DeletePostUseCase
import com.hlasoftware.focus.features.posts.domain.usecase.GetPostsUseCase
import com.hlasoftware.focus.features.posts.domain.usecase.UpdatePostUseCase
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.profile.domain.usecase.DeleteAccountUseCase
import com.hlasoftware.focus.features.profile.domain.usecase.GetProfileUseCase
import com.hlasoftware.focus.features.profile.domain.usecase.UpdateProfileUseCase
import com.hlasoftware.focus.features.routines.domain.repository.RoutineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ProfileViewModel
    private val getProfileUseCase: GetProfileUseCase = mock()
    private val updateProfileUseCase: UpdateProfileUseCase = mock()
    private val activityRepository: ActivityRepository = mock()
    private val getPostsUseCase: GetPostsUseCase = mock()
    private val createPostUseCase: CreatePostUseCase = mock()
    private val updatePostUseCase: UpdatePostUseCase = mock()
    private val deletePostUseCase: DeletePostUseCase = mock()
    private val deleteAccountUseCase: DeleteAccountUseCase = mock()
    private val routineRepository: RoutineRepository = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileViewModel(
            getProfileUseCase,
            updateProfileUseCase,
            activityRepository,
            getPostsUseCase,
            createPostUseCase,
            updatePostUseCase,
            deletePostUseCase,
            deleteAccountUseCase,
            routineRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `showProfile should update state to Success when use case is successful`() = runTest(testDispatcher.scheduler) {
        // Given
        val userId = "123"
        val profile = ProfileModel(uid = userId, name = "Harold", summary = "Developer")
        whenever(getProfileUseCase.invoke(userId)).thenReturn(Result.success(profile))
        whenever(getPostsUseCase(userId)).thenReturn(flowOf(emptyList()))
        whenever(routineRepository.getRoutines()).thenReturn(flowOf(emptyList()))

        // When
        viewModel.showProfile(userId)
        advanceUntilIdle() // Esto asegura que todas las corrutinas del testDispatcher se completen

        // Then
        val finalState = viewModel.state.value
        assertEquals(ProfileViewModel.ProfileUiState.Success(profile), finalState)
    }
}
