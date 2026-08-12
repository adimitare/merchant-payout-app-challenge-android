package com.example.androidinterview.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PaginatedActivityResponseTest {
    @Test
    fun `creates response with items and next cursor`() {
        val activity = ActivityItem(
            id = "1",
            type = "PAYOUT",
            amount = 100,
            currency = Currency.GBP,
            date = "2024-03-01",
            description = "Test payout",
            status = "COMPLETED"
        )
        val response = PaginatedActivityResponse(
            items = listOf(activity),
            next_cursor = "cursor-2",
            has_more = true
        )
        assertEquals(listOf(activity), response.items)
        assertEquals("cursor-2", response.next_cursor)
        assertTrue(response.has_more)
    }

    @Test
    fun `creates final page with no next cursor`() {
        val response = PaginatedActivityResponse(
            items = emptyList(),
            next_cursor = null,
            has_more = false
        )

        assertTrue(response.items.isEmpty())
        assertNull(response.next_cursor)
        assertEquals(false, response.has_more)
    }

    @Test
    fun `response copy updates pagination state`() {
        val response = PaginatedActivityResponse(
            items = emptyList(),
            next_cursor = "cursor-2",
            has_more = true
        )
        val copy = response.copy(
            next_cursor = null,
            has_more = false
        )
        assertNull(copy.next_cursor)
        assertEquals(false, copy.has_more)
    }
}