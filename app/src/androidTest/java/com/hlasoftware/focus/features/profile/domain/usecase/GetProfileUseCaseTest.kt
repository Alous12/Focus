
package com.hlasoftware.focus.features.profile.domain.usecase

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hlasoftware.focus.features.profile.domain.model.ProfileModel
import com.hlasoftware.focus.features.profile.domain.repository.IProfileRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class GetProfileUseCaseIntegrationTest {

    private lateinit var getProfileUseCase: GetProfileUseCase
    private val repository: IProfileRepository = mock()

    @Before
    fun setUp() {
        getProfileUseCase = GetProfileUseCase(repository)
    }

    @Test
    fun invoke_should_return_profile_from_repository() = runBlocking {
        // Given
        val userId = "1"
        val expectedProfile = ProfileModel(uid = userId, name = "Test User", summary = "Summary")
        whenever(repository.fetchData(userId)).thenReturn(Result.success(expectedProfile))

        // When
        val result = getProfileUseCase.invoke(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedProfile, result.getOrNull())
    }
}
