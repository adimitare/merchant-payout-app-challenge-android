package com.example.androidinterview.ui.transactions

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import com.example.androidinterview.domain.model.ActivityItem
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.domain.usecase.TransactionDateLabel
import com.example.androidinterview.util.formatMoney
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import kotlin.intArrayOf

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TransactionListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel = mockk<TransactionViewModel>(relaxed = true)

    private val transactions =
        MutableStateFlow<PagingData<TransactionListItem>>(
            PagingData.empty()
        )

    private fun setContent(
        pagingData: PagingData<TransactionListItem> =
            PagingData.empty()
    ) {
        transactions.value = pagingData

        every {
            viewModel.transactions
        } returns transactions

        composeTestRule.setContent {
            TransactionListScreen(
                onClose = {},
                viewModel = viewModel
            )
        }
    }

    // -------------------------------------------------------------------------
    // Header
    // -------------------------------------------------------------------------
    @Test
    fun screen_displaysTransactionsHeader() {
        setContent()
        composeTestRule
            .onNodeWithText(
                "Transactions"
            )
            .assertIsDisplayed()
    }

    @Test
    fun closeButton_callsOnClose() {
        var closeCalled = false

        transactions.value = PagingData.empty()

        every {
            viewModel.transactions
        } returns transactions

        composeTestRule.setContent {
            TransactionListScreen(
                onClose = {
                    closeCalled = true
                },
                viewModel = viewModel
            )
        }
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()
        assert(closeCalled)
    }

    // -------------------------------------------------------------------------
    // Empty state
    // -------------------------------------------------------------------------

    @Test
    fun emptyState_displaysEmptyMessage() {
        setContent(
            PagingData.empty()
        )

        composeTestRule
            .onNodeWithText("No transactions yet")
            .assertIsDisplayed()
    }

    // -------------------------------------------------------------------------
    // Transactions
    // -------------------------------------------------------------------------

    @Test
    fun transactions_areDisplayed() {
        val transaction = transaction(
            description = "Coffee",
            type = "Card payment",
            amount = 500,
            currency = Currency.GBP,
            status = "COMPLETED"
        )

        setContent(
            PagingData.from(
                listOf(
                    TransactionListItem.Transaction(transaction)
                )
            )
        )

        composeTestRule
            .onNodeWithText("Coffee")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Card payment")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("COMPLETED")
            .assertIsDisplayed()
    }

    @Test
    fun multipleTransactions_areDisplayed() {
        val first = transaction(
            id = "1",
            description = "Coffee",
            type = "Card payment"
        )

        val second = transaction(
            id = "2",
            description = "Salary",
            type = "Bank transfer"
        )

        setContent(
            PagingData.from(
                listOf(
                    TransactionListItem.Transaction(first),
                    TransactionListItem.Transaction(second)
                )
            )
        )

        composeTestRule
            .onNodeWithText("Coffee")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Salary")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Card payment")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Bank transfer")
            .assertIsDisplayed()
    }

    // -------------------------------------------------------------------------
    // Date headers
    // -------------------------------------------------------------------------

    @Test
    fun todayHeader_isDisplayed() {
        setContent(
            PagingData.from(
                listOf(
                    TransactionListItem.Header(
                        date = TransactionDateLabel.Today
                    ),
                    TransactionListItem.Transaction(
                        transaction()
                    )
                )
            )
        )

        composeTestRule
            .onNodeWithText(
                "Today"
            )
            .assertIsDisplayed()
    }

    @Test
    fun yesterdayHeader_isDisplayed() {
        setContent(
            PagingData.from(
                listOf(
                    TransactionListItem.Header(
                        date = TransactionDateLabel.Yesterday
                    ),
                    TransactionListItem.Transaction(
                        transaction()
                    )
                )
            )
        )

        composeTestRule
            .onNodeWithText(
                "Yesterday"
            )
            .assertIsDisplayed()
    }

    @Test
    fun dateHeader_isDisplayed() {
        val date = LocalDate.of(
            2026,
            8,
            10
        )
        setContent(
            PagingData.from(
                listOf(
                    TransactionListItem.Header(
                        date = TransactionDateLabel.Date(date)
                    ),
                    TransactionListItem.Transaction(
                        transaction()
                    )
                )
            )
        )

        composeTestRule
            .onNodeWithText("10 Aug 2026")
            .assertIsDisplayed()
    }

    // -------------------------------------------------------------------------
    // Amounts
    // -------------------------------------------------------------------------
    @Test
    fun positiveTransaction_displaysAmount() {
        val transaction = transaction(
            amount = 10000,
            currency = Currency.GBP
        )

        setContent(
            PagingData.from(
                listOf(
                    TransactionListItem.Transaction(transaction)
                )
            )
        )

        composeTestRule
            .onNodeWithText("£100.00")
            .assertIsDisplayed()
    }

    @Test
    fun negativeTransaction_displaysAmount() {
        val transaction = transaction(
            amount = -2550,
            currency = Currency.GBP
        )

        setContent(
            PagingData.from(
                listOf(
                    TransactionListItem.Transaction(transaction)
                )
            )
        )

        val expectedAmount = formatMoney(
            amount = -2550,
            currency = Currency.GBP
        )

        composeTestRule
            .onNodeWithText(expectedAmount)
            .assertIsDisplayed()
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    private fun transaction(
        id: String = "1",
        description: String = "Coffee",
        type: String = "Card payment",
        amount: Int = 1000,
        currency: Currency = Currency.GBP,
        status: String = "COMPLETED"
    ): ActivityItem {
        return ActivityItem(
            id = id,
            description = description,
            type = type,
            amount = amount,
            currency = currency,
            status = status,
            date = "2026-08-12T10:00:00Z"
        )
    }
}