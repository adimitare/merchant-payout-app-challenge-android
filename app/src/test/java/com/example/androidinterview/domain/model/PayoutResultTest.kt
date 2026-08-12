package com.example.androidinterview.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PayoutResultTest {
    @Test
    fun `creates payout result with expected values`() {
        val result = PayoutResult(
            id = "payout-1",
            amount = 500,
            currency = Currency.GBP,
            iban = "GB00TEST123",
            status = PayoutStatus.COMPLETED,
            createdAt = "2024-03-01T10:00:00Z"
        )
        assertEquals("payout-1", result.id)
        assertEquals(500, result.amount)
        assertEquals(Currency.GBP, result.currency)
        assertEquals("GB00TEST123", result.iban)
        assertEquals(PayoutStatus.COMPLETED, result.status)
        assertEquals("2024-03-01T10:00:00Z", result.createdAt)
    }

    @Test
    fun `payout results with same values are equal`() {
        val first = PayoutResult(
            id = "payout-1",
            amount = 500,
            currency = Currency.GBP,
            iban = "GB00TEST123",
            status = PayoutStatus.COMPLETED,
            createdAt = "2024-03-01T10:00:00Z"
        )
        val second = first.copy()
        assertEquals(first, second)
    }

    @Test
    fun `payout results with different statuses are not equal`() {
        val first = PayoutResult(
            id = "payout-1",
            amount = 500,
            currency = Currency.GBP,
            iban = "GB00TEST123",
            status = PayoutStatus.PENDING,
            createdAt = "2024-03-01T10:00:00Z"
        )
        val second = first.copy(
            status = PayoutStatus.COMPLETED
        )
        assertNotEquals(first, second)
    }
}