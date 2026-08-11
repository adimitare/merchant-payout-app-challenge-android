package com.example.androidinterview.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TransactionDateTest {
    @Test
    fun `transactionLocalDate converts ISO date to local date`() {
        val result = transactionLocalDate("2026-08-11T10:15:30Z")
        assertEquals(
            LocalDate.of(2026, 8, 11),
            result
        )
    }

    @Test
    fun `transactionDateLabel returns Today for today's date`() {
        val today = LocalDate.now()
        assertEquals(
            TransactionDateLabel.Today,
            transactionDateLabel(today)
        )
    }

    @Test
    fun `transactionDateLabel returns Yesterday for yesterday's date`() {
        val yesterday = LocalDate.now().minusDays(1)
        assertEquals(
            TransactionDateLabel.Yesterday,
            transactionDateLabel(yesterday)
        )
    }

    @Test
    fun `transactionDateLabel returns Date for older date`() {
        val date = LocalDate.now().minusDays(5)
        assertEquals(
            TransactionDateLabel.Date(date),
            transactionDateLabel(date)
        )
    }
}