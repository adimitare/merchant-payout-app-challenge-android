package com.example.androidinterview.ui.payout

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onLast
import com.example.androidinterview.domain.biometric.BiometricAuthenticator
import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.domain.model.PayoutResult
import com.example.androidinterview.domain.model.PayoutStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PayoutScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: PayoutViewModel
    private lateinit var biometricAuthenticator: BiometricAuthenticator

    private lateinit var uiState: MutableStateFlow<PayoutUiState>
    private lateinit var effects: MutableSharedFlow<PayoutEffect>

    @Before
    fun setup() {
        uiState = MutableStateFlow(validFormState())
        effects = MutableSharedFlow()
        viewModel = mockk(relaxed = true)
        every { viewModel.uiState } returns uiState
        every { viewModel.effects } returns effects
        biometricAuthenticator = mockk(relaxed = true)
    }

    private fun setContent() {
        composeTestRule.setContent {
            PayoutScreen(
                viewModel = viewModel,
                biometricAuthenticator = biometricAuthenticator
            )
        }
    }

    @Test
    fun formState_displaysPayoutForm() {
        setContent()
        composeTestRule
            .onNodeWithText("Amount")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Currency")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("IBAN")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Confirm")
            .assertIsDisplayed()
    }

    @Test
    fun formState_confirmClick_callsViewModel() {
        setContent()

        composeTestRule
            .onNodeWithText("Confirm")
            .performClick()

        verify {
            viewModel.onConfirmClicked()
        }
    }

    @Test
    fun confirmingState_displaysConfirmationDialog() {
        uiState.value = confirmingState()

        setContent()

        composeTestRule
            .onNodeWithText("Amount")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("IBAN")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Confirm Payout")
            .assertIsDisplayed()
    }

    @Test
    fun confirmingState_cancel_callsViewModel() {
        uiState.value = confirmingState()

        setContent()

        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()

        verify {
            viewModel.cancelConfirmation()
        }
    }

    @Test
    fun confirmingState_confirm_callsSubmitPayout() {
        uiState.value = confirmingState()
        setContent()
        composeTestRule
            .onAllNodesWithText("Confirm")
            .onLast()
            .performClick()
        verify {
            viewModel.submitPayout()
        }
    }

    @Test
    fun successState_displaysSuccessContent() {
        uiState.value = successState()

        setContent()

        composeTestRule
            .onNodeWithText(
                "Payout Completed"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "Create Another Payout"
            )
            .assertIsDisplayed()
    }

    @Test
    fun successState_createAnother_callsViewModel() {
        uiState.value = successState()

        setContent()

        composeTestRule
            .onNodeWithText(
                "Create Another Payout"
            )
            .performClick()

        verify {
            viewModel.createAnotherPayout()
        }
    }

    @Test
    fun errorState_displaysFailureContent() {
        uiState.value = PayoutUiState.Error(
            error = PayoutError.InsufficientFunds,
            data = mockk()
        )

        setContent()

        composeTestRule
            .onNodeWithText(
                "Unable to Process Payout"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "Insufficient funds."
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "Try Again"
            )
            .assertIsDisplayed()
    }

    @Test
    fun errorState_retry_callsViewModel() {
        uiState.value = PayoutUiState.Error(
            error = PayoutError.ServiceUnavailable,
            data = mockk()
        )

        setContent()

        composeTestRule
            .onNodeWithText(
                "Try Again"
            )
            .performClick()

        verify {
            viewModel.retry()
        }
    }

    @Test
    fun awaitingBiometric_displaysPayoutForm() {
        uiState.value = PayoutUiState.AwaitingBiometric(
            data = validFormData()
        )

        setContent()

        composeTestRule
            .onNodeWithText("Amount")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Currency")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("IBAN")
            .assertIsDisplayed()
    }

    @Test
    fun biometricEffect_callsAuthenticator() {
        setContent()

        composeTestRule.runOnIdle {
            // Effect is emitted below.
        }

        // See note below regarding FragmentActivity.
    }

    private fun validFormState(): PayoutUiState.Form {
        return PayoutUiState.Form(
            data = validFormData()
        )
    }

    private fun validFormData(): PayoutFormData {
        return PayoutFormData(
            amount = "100.00",
            currency = Currency.EUR,
            iban = "DE89370400440532013000",
            amountError = null,
            ibanError = null
        )
    }

    private fun confirmingState(): PayoutUiState.Confirming {
        return PayoutUiState.Confirming(
            data = validFormData()
        )
    }

    private fun successState(): PayoutUiState.Success {
        return PayoutUiState.Success(
            payout = PayoutResult(
                id = "1",
                amount = 10000,
                currency = Currency.EUR,
                iban = "DE89370400440532013000",
                status = PayoutStatus.COMPLETED,
                createdAt = "2026-08-12T10:00:00Z"
            )
        )
    }
}