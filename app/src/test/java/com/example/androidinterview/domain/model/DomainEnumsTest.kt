package com.example.androidinterview.domain.model

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class DomainEnumsTest {
    @Test
    fun `activity types contain expected values`() {
        assertArrayEquals(
            arrayOf(
                ActivityType.PAYOUT,
                ActivityType.DEPOSIT,
                ActivityType.REFUND,
                ActivityType.FEE
            ),
            ActivityType.entries.toTypedArray()
        )
    }

    @Test
    fun `activity statuses contain expected values`() {
        assertArrayEquals(
            arrayOf(
                ActivityStatus.COMPLETED,
                ActivityStatus.PENDING,
                ActivityStatus.PROCESSING,
                ActivityStatus.FAILED
            ),
            ActivityStatus.entries.toTypedArray()
        )
    }

    @Test
    fun `currencies contain supported values`() {
        assertArrayEquals(
            arrayOf(
                Currency.GBP,
                Currency.EUR
            ),
            Currency.entries.toTypedArray()
        )
    }

    @Test
    fun `payout statuses contain expected values`() {
        assertArrayEquals(
            arrayOf(
                PayoutStatus.COMPLETED,
                PayoutStatus.FAILED,
                PayoutStatus.PENDING
            ),
            PayoutStatus.entries.toTypedArray()
        )
    }
}