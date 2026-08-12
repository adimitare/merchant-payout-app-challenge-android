package com.example.androidinterview.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PayoutExceptionTest {
    @Test
    fun `insufficient funds is correct exception type`() {
        val exception = PayoutException.InsufficientFunds
        assertTrue(exception is PayoutException)
    }

    @Test
    fun `service unavailable is correct exception type`() {
        val exception = PayoutException.ServiceUnavailable
        assertTrue(exception is PayoutException)
    }

    @Test
    fun `api error contains error message`() {
        val exception = PayoutException.ApiError(
            errorMessage = "Something went wrong"
        )
        assertEquals(
            "Something went wrong",
            exception.errorMessage
        )
    }

    @Test
    fun `api error is a payout exception`() {
        val exception = PayoutException.ApiError(
            errorMessage = "API error"
        )
        assertTrue(exception is PayoutException)
    }

    @Test
    fun `payout exceptions are throwable`() {
        val exception = PayoutException.ApiError(
            errorMessage = "API error"
        )
        assertTrue(exception is Exception)
    }
}