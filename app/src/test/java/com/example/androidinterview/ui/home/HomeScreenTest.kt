package com.example.androidinterview.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.example.androidinterview.R
import com.example.androidinterview.domain.model.ActivityItem
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.domain.model.Merchant
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeScreenTest {
    @get:Rule
    val composeTestRule = androidx.compose.ui.test.junit4.v2.createComposeRule()
/*    @Test
    fun `Loading state shows loading content`() {
        // Given
        val viewModel = mockHomeViewModel(
            state = HomeUiState.Loading
        )

        // When
        composeTestRule.setContent {
            HomeScreen(
                onOpenTransactions = {},
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.loading)
            )
            .assertIsDisplayed()
    }*/

    @Test
    fun `Error state shows error message`() {
        // Given
        val errorMessage = "Unable to load merchant"

        val viewModel = mockHomeViewModel(
            state = HomeUiState.Error(errorMessage)
        )

        // When
        composeTestRule.setContent {
            HomeScreen(
                onOpenTransactions = {},
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule
            .onNodeWithText(errorMessage)
            .assertIsDisplayed()
    }

    @Test
    fun `Error state retry calls loadMerchant`() {
        // Given
        val viewModel = mockHomeViewModel(
            state = HomeUiState.Error("Unable to load merchant")
        )

        // When
        composeTestRule.setContent {
            HomeScreen(
                onOpenTransactions = {},
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Retry")
            .performClick()

        verify(exactly = 1) {
            viewModel.loadMerchant()
        }
    }

    @Test
    fun `Success state shows merchant balance`() {
        // Given
        val merchant = merchant()

        val viewModel = mockHomeViewModel(
            state = HomeUiState.Success(merchant)
        )
        // When
        composeTestRule.setContent {
            HomeScreen(
                onOpenTransactions = {},
                viewModel = viewModel
            )
        }
        // Then
        composeTestRule
            .onNodeWithText("Business Account")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Account Balance")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("£10.00")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("£2.50")
            .assertIsDisplayed()
    }

    @Test
    fun `Success state shows recent activity`() {
        // Given
        val merchant = merchant(
            activity = listOf(
                ActivityItem(
                    id = "activity-1",
                    type = "Payout",
                    amount = 500,
                    currency = Currency.GBP,
                    date = "2024-01-15",
                    description = "Bank transfer",
                    status = "Completed"
                )
            )
        )

        val viewModel = mockHomeViewModel(
            state = HomeUiState.Success(merchant)
        )

        // When
        composeTestRule.setContent {
            HomeScreen(
                onOpenTransactions = {},
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule
            .onNode(hasScrollAction())
            .performScrollToNode(
                hasText("Recent Activity")
            )

        composeTestRule
            .onNodeWithText("Recent Activity")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Bank transfer")
            .assertIsDisplayed()
    }

    @Test
    fun `Success state shows multiple activities`() {
        // Given
        val merchant = merchant(
            activity = listOf(
                ActivityItem(
                    id = "activity-1",
                    type = "Payout",
                    amount = 500,
                    currency = Currency.GBP,
                    date = "2024-01-15",
                    description = "Bank transfer",
                    status = "Completed"
                ),
                ActivityItem(
                    id = "activity-2",
                    type = "Fee",
                    amount = 25,
                    currency = Currency.GBP,
                    date = "2024-01-16",
                    description = "Transaction fee",
                    status = "Completed"
                )
            )
        )

        val viewModel = mockHomeViewModel(
            state = HomeUiState.Success(merchant)
        )

        // When
        composeTestRule.setContent {
            HomeScreen(
                onOpenTransactions = {},
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Bank transfer")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Transaction fee")
            .assertIsDisplayed()
    }

    @Test
    fun `Show more button invokes onOpenTransactions`() {
        // Given
        val viewModel = mockHomeViewModel(
            state = HomeUiState.Success(merchant())
        )

        var clicked = false

        // When
        composeTestRule.setContent {
            HomeScreen(
                onOpenTransactions = {
                    clicked = true
                },
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Show More")
            .assertIsDisplayed()
            .performClick()
        assert(clicked)
    }

    private fun mockHomeViewModel(
        state: HomeUiState
    ): HomeViewModel {
        val viewModel = mockk<HomeViewModel>(relaxed = true)

        every {
            viewModel.uiState
        } returns MutableStateFlow(state)

        return viewModel
    }

    private fun merchant(
        activity: List<ActivityItem> = emptyList()
    ): Merchant {
        return Merchant(
            availableBalance = 1000,
            pendingBalance = 250,
            currency = Currency.GBP,
            activityItem = activity
        )
    }
}