package com.example.androidinterview.ui.payout

import com.example.androidinterview.domain.model.Currency
import com.example.androidinterview.domain.model.PayoutResult
import com.example.androidinterview.domain.model.PayoutStatus
import com.example.androidinterview.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PayoutUiStateTest {
    @Test
    fun payoutFormData_hasCorrectDefaultValues() {
        val data = PayoutFormData()

        assertEquals("", data.amount)
        assertEquals(Currency.GBP, data.currency)
        assertEquals("", data.iban)
        assertEquals(null, data.amountError)
        assertEquals(null, data.ibanError)
    }

    @Test
    fun payoutFormData_storesProvidedValues() {
        val data = PayoutFormData(
            amount = "100.50",
            currency = Currency.EUR,
            iban = "DE89370400440532013000",
            amountError = R.string.enter_a_valid_amount,
            ibanError = R.string.enter_an_amount
        )

        assertEquals("100.50", data.amount)
        assertEquals(Currency.EUR, data.currency)
        assertEquals("DE89370400440532013000", data.iban)
        assertEquals(R.string.enter_a_valid_amount, data.amountError)
        assertEquals(R.string.enter_an_amount, data.ibanError)
    }

    @Test
    fun payoutFormData_copy_updatesOnlySpecifiedValue() {
        val original = PayoutFormData(
            amount = "100.00",
            currency = Currency.GBP,
            iban = "GB82WEST12345698765432"
        )

        val updated = original.copy(
            amount = "200.00"
        )

        assertEquals("100.00", original.amount)
        assertEquals("200.00", updated.amount)

        assertEquals(original.currency, updated.currency)
        assertEquals(original.iban, updated.iban)
        assertEquals(original.amountError, updated.amountError)
        assertEquals(original.ibanError, updated.ibanError)
    }

    @Test
    fun payoutFormData_withError_canBeCopiedWithoutError() {
        val dataWithError = PayoutFormData(
            amount = "invalid",
            amountError = R.string.enter_a_valid_amount
        )

        val validData = dataWithError.copy(
            amount = "100.00",
            amountError = null
        )

        assertEquals("invalid", dataWithError.amount)
        assertEquals(R.string.enter_a_valid_amount, dataWithError.amountError)

        assertEquals("100.00", validData.amount)
        assertEquals(null, validData.amountError)
    }

    // -------------------------------------------------------------------------
    // Form
    // -------------------------------------------------------------------------

    @Test
    fun form_hasDefaultFormData() {
        val state = PayoutUiState.Form()

        assertEquals(PayoutFormData(), state.data)
    }

    @Test
    fun form_storesProvidedData() {
        val data = validFormData()

        val state = PayoutUiState.Form(
            data = data
        )

        assertEquals(data, state.data)
    }

    @Test
    fun form_copy_updatesData() {
        val state = PayoutUiState.Form(
            data = validFormData()
        )

        val updated = state.copy(
            data = state.data.copy(
                amount = "200.00"
            )
        )

        assertEquals("100.00", state.data.amount)
        assertEquals("200.00", updated.data.amount)
        assertEquals(state.data.currency, updated.data.currency)
        assertEquals(state.data.iban, updated.data.iban)
    }

    // -------------------------------------------------------------------------
    // Confirming
    // -------------------------------------------------------------------------

    @Test
    fun confirming_storesFormData() {
        val data = validFormData()

        val state = PayoutUiState.Confirming(
            data = data
        )

        assertEquals(data, state.data)
    }

    @Test
    fun confirming_isPayoutUiState() {
        val state: PayoutUiState = PayoutUiState.Confirming(
            data = validFormData()
        )

        assertTrue(state is PayoutUiState.Confirming)
    }

    // -------------------------------------------------------------------------
    // AwaitingBiometric
    // -------------------------------------------------------------------------

    @Test
    fun awaitingBiometric_storesFormData() {
        val data = validFormData()

        val state = PayoutUiState.AwaitingBiometric(
            data = data
        )

        assertEquals(data, state.data)
    }

    @Test
    fun awaitingBiometric_isPayoutUiState() {
        val state: PayoutUiState =
            PayoutUiState.AwaitingBiometric(
                data = validFormData()
            )

        assertTrue(state is PayoutUiState.AwaitingBiometric)
    }

    // -------------------------------------------------------------------------
    // Submitting
    // -------------------------------------------------------------------------

    @Test
    fun submitting_isPayoutUiState() {
        val state: PayoutUiState = PayoutUiState.Submitting

        assertTrue(state is PayoutUiState.Submitting)
    }

    @Test
    fun submitting_isSingleton() {
        val first = PayoutUiState.Submitting
        val second = PayoutUiState.Submitting

        assertEquals(first, second)
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    fun success_storesPayoutResult() {
        val payout = payoutResult()

        val state = PayoutUiState.Success(
            payout = payout
        )

        assertEquals(payout, state.payout)
    }

    @Test
    fun success_copy_updatesPayout() {
        val firstPayout = payoutResult(
            amount = 10000
        )

        val secondPayout = payoutResult(
            amount = 20000
        )

        val state = PayoutUiState.Success(
            payout = firstPayout
        )

        val updated = state.copy(
            payout = secondPayout
        )

        assertEquals(firstPayout, state.payout)
        assertEquals(secondPayout, updated.payout)
    }

    // -------------------------------------------------------------------------
    // Error
    // -------------------------------------------------------------------------

    @Test
    fun error_storesErrorAndFormData() {
        val data = validFormData()

        val state = PayoutUiState.Error(
            error = PayoutError.InsufficientFunds,
            data = data
        )

        assertEquals(PayoutError.InsufficientFunds, state.error)
        assertEquals(data, state.data)
    }

    @Test
    fun error_canRepresentDifferentPayoutErrors() {
        val data = validFormData()

        val insufficientFunds = PayoutUiState.Error(
            error = PayoutError.InsufficientFunds,
            data = data
        )

        val serviceUnavailable = PayoutUiState.Error(
            error = PayoutError.ServiceUnavailable,
            data = data
        )

        assertNotEquals(
            insufficientFunds,
            serviceUnavailable
        )
    }

    @Test
    fun error_copy_updatesOnlyError() {
        val data = validFormData()

        val original = PayoutUiState.Error(
            error = PayoutError.InsufficientFunds,
            data = data
        )

        val updated = original.copy(
            error = PayoutError.ServiceUnavailable
        )

        assertEquals(
            PayoutError.InsufficientFunds,
            original.error
        )

        assertEquals(
            PayoutError.ServiceUnavailable,
            updated.error
        )

        assertEquals(
            data,
            updated.data
        )
    }

    @Test
    fun error_copy_updatesOnlyFormData() {
        val originalData = validFormData()

        val original = PayoutUiState.Error(
            error = PayoutError.InsufficientFunds,
            data = originalData
        )

        val updatedData = originalData.copy(
            amount = "200.00"
        )

        val updated = original.copy(
            data = updatedData
        )

        assertEquals(
            PayoutError.InsufficientFunds,
            updated.error
        )

        assertEquals(
            "200.00",
            updated.data.amount
        )
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun validFormData(): PayoutFormData {
        return PayoutFormData(
            amount = "100.00",
            currency = Currency.EUR,
            iban = "DE89370400440532013000"
        )
    }

    private fun payoutResult(
        amount: Int = 10000
    ): PayoutResult {
        return PayoutResult(
            id = "1",
            amount = amount,
            currency = Currency.EUR,
            iban = "DE89370400440532013000",
            status = PayoutStatus.COMPLETED,
            createdAt = "2026-08-12T10:00:00Z"
        )
    }
}