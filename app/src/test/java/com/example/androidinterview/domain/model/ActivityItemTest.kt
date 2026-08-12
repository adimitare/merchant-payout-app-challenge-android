package com.example.androidinterview.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ActivityItemTest {
    @Test
    fun `creates activity item with expected values`() {
        val activity = ActivityItem(
            id = "activity-1",
            type = "PAYOUT",
            amount = 100,
            currency = Currency.GBP,
            date = "2024-03-01",
            description = "Test payout",
            status = "COMPLETED"
        )

        assertEquals("activity-1", activity.id)
        assertEquals("PAYOUT", activity.type)
        assertEquals(100, activity.amount)
        assertEquals(Currency.GBP, activity.currency)
        assertEquals("2024-03-01", activity.date)
        assertEquals("Test payout", activity.description)
        assertEquals("COMPLETED", activity.status)
    }

    @Test
    fun `activity items with same values are equal`() {
        val first = ActivityItem(
            id = "1",
            type = "PAYOUT",
            amount = 100,
            currency = Currency.GBP,
            date = "2024-03-01",
            description = "Test",
            status = "COMPLETED"
        )

        val second = first.copy()

        assertEquals(first, second)
    }

    @Test
    fun `activity items with different ids are not equal`() {
        val first = ActivityItem(
            id = "1",
            type = "PAYOUT",
            amount = 100,
            currency = Currency.GBP,
            date = "2024-03-01",
            description = "Test",
            status = "COMPLETED"
        )

        val second = first.copy(id = "2")

        assertNotEquals(first, second)
    }
}