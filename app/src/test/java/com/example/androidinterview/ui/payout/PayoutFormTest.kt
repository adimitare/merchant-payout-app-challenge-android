package com.example.androidinterview.ui.payout

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.androidinterview.R
import com.example.androidinterview.domain.model.Currency
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PayoutFormTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val onAmountChanged = mockk<(String) -> Unit>(relaxed = true)
    private val onCurrencyChanged = mockk<(Currency) -> Unit>(relaxed = true)
    private val onIbanChanged = mockk<(String) -> Unit>(relaxed = true)
    private val onConfirm = mockk<() -> Unit>(relaxed = true)

    @Test
    fun amountField_callsOnAmountChanged() {
        setContent(
            state = validState(amount = "")
        )
        composeTestRule
            .onNodeWithText("Amount")
            .performTextInput("100")
        verify {
            onAmountChanged("100")
        }
    }

    @Test
    fun ibanField_callsOnIbanChanged() {
        setContent(
            state = validState(iban = "")
        )
        composeTestRule
            .onNodeWithText("IBAN")
            .performTextInput("DE123456789")
        verify {
            onIbanChanged("DE123456789")
        }
    }

    @Test
    fun selectingCurrency_callsOnCurrencyChanged() {
        setContent(
            state = validState()
        )
        composeTestRule
            .onNodeWithText(Currency.EUR.name)
            .performClick()
        composeTestRule
            .onNodeWithText(Currency.GBP.name)
            .performClick()
        verify {
            onCurrencyChanged(Currency.GBP)
        }
    }

    @Test
    fun confirmButton_callsOnConfirm() {
        setContent(
            state = validState()
        )
        composeTestRule
            .onNodeWithText("Confirm")
            .performClick()
        verify {
            onConfirm()
        }
    }

    @Test
    fun confirmButton_isDisabled_whenAmountIsBlank() {
        setContent(
            state = validState(amount = "")
        )
        composeTestRule
            .onNodeWithText("Confirm")
            .assertIsNotEnabled()

        verify(exactly = 0) {
            onConfirm()
        }
    }

    @Test
    fun confirmButton_isDisabled_whenIbanIsBlank() {
        setContent(
            state = validState(iban = "")
        )
        composeTestRule
            .onNodeWithText("Confirm")
            .assertIsNotEnabled()
        verify(exactly = 0) {
            onConfirm()
        }
    }

    @Test
    fun confirmButton_isDisabled_whenAmountHasError() {
        setContent(
            state = validState(
                amountError = R.string.amount_must_be_greater_than_zero
            )
        )
        composeTestRule
            .onNodeWithText("Confirm")
            .assertIsNotEnabled()
        verify(exactly = 0) {
            onConfirm()
        }
    }

    @Test
    fun confirmButton_isDisabled_whenIbanHasError() {
        setContent(
            state = validState(
                ibanError = R.string.enter_a_valid_iban
            )
        )
        composeTestRule
            .onNodeWithText("Confirm")
            .assertIsNotEnabled()
        verify(exactly = 0) {
            onConfirm()
        }
    }

    private fun setContent(
        state: PayoutUiState.Form
    ) {
        composeTestRule.setContent {
            PayoutForm(
                state = state,
                onAmountChanged = onAmountChanged,
                onCurrencyChanged = onCurrencyChanged,
                onIbanChanged = onIbanChanged,
                onConfirm = onConfirm
            )
        }
    }

    private fun validState(
        amount: String = "100.00",
        iban: String = "DE89370400440532013000",
        currency: Currency = Currency.EUR,
        amountError: Int? = null,
        ibanError: Int? = null
    ): PayoutUiState.Form {
        return PayoutUiState.Form(
            data = PayoutFormData(
                amount = amount,
                currency = currency,
                iban = iban,
                amountError = amountError,
                ibanError = ibanError
            )
        )
    }
}