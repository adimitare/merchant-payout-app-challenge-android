package com.example.androidinterview.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MerchantTest {
    @Test
    fun `creates merchant with expected values`() {
        val activity = ActivityItem(
            id = "1",
            type = "PAYOUT",
            amount = 100,
            currency = Currency.GBP,
            date = "2024-03-01",
            description = "Test payout",
            status = "COMPLETED"
        )
        val merchant = Merchant(
            availableBalance = 1000,
            pendingBalance = 200,
            currency = Currency.GBP,
            activityItem = listOf(activity)
        )
        assertEquals(1000, merchant.availableBalance)
        assertEquals(200, merchant.pendingBalance)
        assertEquals(Currency.GBP, merchant.currency)
        assertEquals(listOf(activity), merchant.activityItem)
    }

    @Test
    fun `merchant can contain empty activity list`() {
        val merchant = Merchant(
            availableBalance = 1000,
            pendingBalance = 200,
            currency = Currency.GBP,
            activityItem = emptyList()
        )
        assertTrue(merchant.activityItem.isEmpty())
    }

    @Test
    fun `merchant copy preserves values`() {
        val merchant = Merchant(
            availableBalance = 1000,
            pendingBalance = 200,
            currency = Currency.GBP,
            activityItem = emptyList()
        )
        val copy = merchant.copy(
            availableBalance = 1500
        )
        assertEquals(1500, copy.availableBalance)
        assertEquals(200, copy.pendingBalance)
        assertEquals(Currency.GBP, copy.currency)
        assertEquals(emptyList<ActivityItem>(), copy.activityItem)
    }
}