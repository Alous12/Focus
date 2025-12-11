
package com.hlasoftware.focus.features.home.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hlasoftware.focus.features.home.domain.model.ActivityModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun activitiesList_isDisplayed_whenStateIsSuccessWithActivities() {
        // Given
        val activities = listOf(
            ActivityModel(id = "1", title = "Activity 1", type = "CLASS"),
            ActivityModel(id = "2", title = "Activity 2", type = "TASK"),
        )
        val homeViewModel = FakeHomeViewModel(HomeUiState.Success(activities))

        // When
        composeTestRule.setContent {
            HomeScreen(
                userId = "testUser",
                homeViewModel = homeViewModel,
                selectedDate = LocalDate.now(),
                onDateChange = {},
                showAddActivitySheet = false,
                onDismissAddActivitySheet = {},
                onActivityClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Activity 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Activity 2").assertIsDisplayed()
    }
}

class FakeHomeViewModel(initialState: HomeUiState) : IHomeViewModel {
    override val uiState: StateFlow<HomeUiState> = MutableStateFlow(initialState)
    override val showDeleteConfirmationDialog: StateFlow<Boolean> = MutableStateFlow(false)

    override fun loadHome(userId: String, date: LocalDate) {}
    override fun createActivity(userId: String, title: String, description: String, date: LocalDate, time: LocalTime?) {}
    override fun onActivityOptionsClicked(activityId: String) {}
    override fun onDeleteActivityClicked(activityId: String) {}
    override fun onConfirmDeleteActivity(userId: String, date: LocalDate) {}
    override fun onDismissDeleteActivity() {}
}
